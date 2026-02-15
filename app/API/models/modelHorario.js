const mongoose = require('mongoose')

const horariosSchema = new mongoose.Schema({
    turma: {
        type: String,
        required: true
    },
    curso: {
        type: String,
        required: true
    },
    ano: {
        type: String,
        required: true
    },
    horario:
    {
        type: String,
        required: true
    },  


})

module.exports = mongoose.model('Horario', horariosSchema)