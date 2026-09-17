#!/usr/bin/env node
// GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json node scripts/fcm-push.mjs --topic verification
// GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json node scripts/fcm-push.mjs --topic disbursement
// GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json node scripts/fcm-push.mjs --topic installment
// GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json node scripts/fcm-push.mjs --topic promo
// GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json node scripts/fcm-push.mjs --fid <installationId>

import { accessToken, serviceAccount } from './fcm-token.mjs'

const TOPICS = ['promo', 'verification', 'disbursement', 'installment']
const FID_PATTERN = /^[A-Za-z0-9_-]{16,}$/
const CHANNELS = ['general', 'transaction', 'promo']
const DEEP_LINK_SCHEME = 'bcaf:'

const DEFAULTS = {
  topic: 'verification',
  title: 'Akun Berhasil Diverifikasi',
  body: 'Akun kamu telah berhasil diverifikasi.',
  channel: 'transaction',
  deeplink: '',
}

const TOPIC_DEFAULTS = {
  promo: {
    title: 'Promo Pinjam100',
    body: 'Ada promo menarik untuk kamu. Ketuk untuk melihat detailnya.',
    channel: 'promo',
    deeplink: '',
  },
  verification: {
    title: 'Akun Berhasil Diverifikasi',
    body: 'Akun kamu telah berhasil diverifikasi.',
    channel: 'transaction',
    deeplink: '',
  },
  disbursement: {
    title: 'Pencairan Berhasil',
    body: 'Dana pinjaman kamu telah berhasil dicairkan.',
    channel: 'transaction',
    deeplink: 'bcaf://disbursement/123',
  },
  installment: {
    title: 'Pembayaran Angsuran Berhasil',
    body: 'Pembayaran angsuran kamu telah berhasil.',
    channel: 'transaction',
    deeplink: 'bcaf://installment/123',
  },
}

const USAGE = `
Trigger push notification FCM HTTP v1 ke topic atau satu perangkat.

  GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json \\
    node scripts/fcm-push.mjs [opsi]

Target (pilih salah satu, default: --topic ${DEFAULTS.topic}):
  --topic <nama>     ${TOPICS.join(' | ')}
  --fid <id>         Firebase installation id satu perangkat

Opsi:
  --deeplink <uri>   Deeplink notifikasi
  --title <teks>     Judul notifikasi
  --body <teks>      Isi notifikasi
  --channel <id>     ${CHANNELS.join(' | ')}
  --project <id>     Default: project_id dari service account
  --dry-run          Validasi payload tanpa mengirim
  --help

Contoh:
  --topic verification
  --topic disbursement
  --topic installment
  --topic promo
  --fid <installationId>
`

function parseArgs(argv) {
  const flags = new Set(['dry-run', 'help'])
  const parsed = {}

  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i]

    if (!arg.startsWith('--')) {
      throw new Error(`Argumen tidak dikenal: ${arg}`)
    }

    const [key, inlineValue] = arg.slice(2).split(/=(.*)/s)

    if (flags.has(key)) {
      parsed[key] = true
      continue
    }

    const value = inlineValue ?? argv[++i]

    if (value === undefined) {
      throw new Error(`Opsi --${key} butuh nilai`)
    }

    parsed[key] = value
  }

  return parsed
}

function buildTarget(args) {
  if (args.fid !== undefined && args.topic !== undefined) {
    throw new Error('Pilih salah satu target: --topic atau --fid')
  }

  if (args.fid !== undefined) {
    if (!FID_PATTERN.test(args.fid)) {
      throw new Error(`Installation id tidak valid: ${args.fid}`)
    }

    return { fid: args.fid }
  }

  const topic = args.topic ?? DEFAULTS.topic

  if (!TOPICS.includes(topic)) {
    throw new Error(
      `Topic "${topic}" tidak dikenal, pilih: ${TOPICS.join(', ')}`
    )
  }

  return { topic }
}

function buildMessage(args) {
  const target = buildTarget(args)

  const defaults = target.topic
    ? TOPIC_DEFAULTS[target.topic]
    : DEFAULTS

  const channel = args.channel ?? defaults.channel

  if (!CHANNELS.includes(channel)) {
    throw new Error(
      `Channel "${channel}" tidak dikenal, pilih: ${CHANNELS.join(', ')}`
    )
  }

  const deeplink = args.deeplink ?? defaults.deeplink

  if (deeplink) {
    const parsedDeepLink = URL.parse(deeplink)

    if (!parsedDeepLink) {
      throw new Error(`Deeplink bukan URI valid: ${deeplink}`)
    }

    if (parsedDeepLink.protocol !== DEEP_LINK_SCHEME) {
      console.warn(
        `Peringatan: scheme "${parsedDeepLink.protocol}" bukan ${DEEP_LINK_SCHEME}.`
      )
    }
  }

  return {
    ...target,
    data: {
      title: args.title ?? defaults.title,
      body: args.body ?? defaults.body,
      channel,
      deeplink,
    },
    android: {
      priority: 'high',
    },
  }
}

async function send(projectId, bearer, message, dryRun) {
  const url =
    `https://fcm.googleapis.com/v1/projects/${projectId}/messages:send`

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${bearer}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        validate_only: Boolean(dryRun),
        message,
      }),
    })

    const text = await res.text()

    let json

    try {
      json = JSON.parse(text)
    } catch {
      json = text
    }

    if (!res.ok) {
      throw new Error(
        `FCM menolak pesan (HTTP ${res.status}):\n${JSON.stringify(json, null, 2)}`
      )
    }

    return json
  } catch (error) {
    console.error('FCM request failed')
    console.error('message:', error.message)
    console.error('cause:', error.cause)
    throw error
  }
}

try {
  const args = parseArgs(process.argv.slice(2))

  if (args.help) {
    console.log(USAGE.trim())
    process.exit(0)
  }

  const message = buildMessage(args)
  const sa = serviceAccount()
  const projectId = args.project ?? sa.project_id

  console.log(JSON.stringify(message, null, 2))

  const result = await send(
    projectId,
    await accessToken(sa),
    message,
    args['dry-run']
  )

  const target = message.fid
    ? `installation id ${message.fid}`
    : `topic ${message.topic}`

  console.log(
    args['dry-run']
      ? 'Payload valid (dry run, tidak dikirim).'
      : `Terkirim ke ${target}: ${result.name}`
  )
} catch (error) {
  console.error(error.message)
  process.exit(1)
}