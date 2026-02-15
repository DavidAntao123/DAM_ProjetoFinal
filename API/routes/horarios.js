const express = require('express')
const router = express.Router()
const Horario = require('../models/modelHorario')


//Get all
router.get('/', async (req, res)=>
{
    try{
        const horarios = await Horario.find()
        res.json(horarios)
    }catch(err)
    {
        res.status(500).json({message: err.message})
    }

})
//Get one
router.get('/:turma',getHorario,(req,res)=>
{
    res.send(res.horario.horario)
})

//Create one
router.post('/',async(req,res)=>
{
    const horario = new Horario({
        turma: req.body.turma,
        curso: req.body.curso,
        ano: req.body.ano,
        horario: req.body.horario
    })
    try{
        const newHorario = await horario.save()
        res.status(201).json(newHorario)
    }catch(err)
    {
        res.status(400).json({message : err.message})
    }
})

//Update one
router.patch('/:turma',getHorario,async (req,res)=>
{
    if(req.body.turma != null)
    {
        res.horario.turma = req.body.turma
    }
    if(req.body.horario != null)
    {
        res.horario.horario = req.body.horario
    }
    try
    {
        const updatedHorario = await res.horario.save()
        res.json(updatedHorario)
    }
    catch (err)
    {
        res.status(400).json({message: err.message})
    }
})

//Delete one
router.delete('/:turma',getHorario,async (req,res)=>
{
    try{
        await res.horario.deleteOne();
        res.json({message: 'Horario removido'})
    } catch(err)
    {
        res.status(500).json({message: err.message})
    }
})

async function getHorario(req,res,next)
{
    let horario
    try{
        horario = await Horario.findOne({turma: req.params.turma})
        if(horario == null){
            return res.status(404).json({message: 'Horario não encontrado'})
        }
    }catch(err){
        return res.status(500).json({message: err.message})
    }
    res.horario = horario
    next()
}

module.exports = router