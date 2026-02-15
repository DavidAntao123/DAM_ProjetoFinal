require('dotenv').config()

const express = require('express')
const app = express()
const mongoose = require('mongoose')


mongoose.connect(process.env.DATABASE_URL)
const db = mongoose.connection
db.on('error', (error) => console.error(error))
db.once('open', () => console.log('Ligado a B.D'))

app.use(express.json())

const horariosRouter = require('./routes/horarios')
app.use('/horarios', horariosRouter)

app.listen(3000, () => console.log('Server iniciado',process.env.DATABASE_URL ))