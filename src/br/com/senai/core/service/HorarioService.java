package br.com.senai.core.service;

import java.time.LocalTime;
import java.util.List;

import br.com.senai.core.dao.DaoHorario;
import br.com.senai.core.dao.FactoryDao;
import br.com.senai.core.domain.Horario;
import br.com.senai.core.domain.Restaurante;

public class HorarioService {

	private DaoHorario daoHorario;

	public HorarioService() {
		this.daoHorario = FactoryDao.getInstance().getDaoHorario();
	}
	
	public Horario buscarPor(int idDoHorario) {
		if (idDoHorario > 0) {
			Horario horarioAtendimentoEncontrado = daoHorario.buscarPor(idDoHorario);
			
			if (horarioAtendimentoEncontrado == null) {
				throw new IllegalArgumentException("Não foi encontrado nenhum restaurante para o código solicitado.");
			}
			return horarioAtendimentoEncontrado;
			
		} else {
			throw new IllegalArgumentException("O id para busca de um restaurante deve ser maior que zero.");
		}
	}

	public List<Horario> listarPor(Restaurante restaurante) {
		if (restaurante == null || restaurante.getId() <= 0) {
			throw new IllegalArgumentException("O restaurante é obrigatório para a listagem dos horários de atendimento.");
		}
		return daoHorario.listarPor(restaurante);
	}

    private boolean horariosDeConflito(LocalTime  inicio, LocalTime fim, LocalTime inicio2, LocalTime fim2) {
        return (inicio.isBefore(fim2) && inicio2.isBefore(fim)) ||
               inicio.equals(fim2) ||
               fim.equals(inicio2);
    }

	
    public void validarHorarioDeConflito(Horario novoHorario, List<Horario> horariosArmazenados) {
        for (Horario horarioArmazenado : horariosArmazenados) {
        	
            if (horarioArmazenado.getDiaDaSemana().equals(novoHorario.getDiaDaSemana())) {
            	
                if (horariosDeConflito(novoHorario.getHorarioInicio(), novoHorario.getHorarioFim(),
                        horarioArmazenado.getHorarioInicio(), horarioArmazenado.getHorarioFim())) {
                    throw new IllegalArgumentException("ERRO: O novo horário conflita com um horário já cadastrado no sistema.");
                }
            }
        }
    }
    
	public void salvar(Horario horario) {
		
		this.validar(horario);

		boolean isPersistido = horario.getId() > 0;

		this.validarHorarioDeConflito(horario, daoHorario.listarPor(horario.getRestaurante()));
		
		if (isPersistido) {
			this.daoHorario.alterar(horario);
		} else {
			this.daoHorario.inserir(horario);
		}
	}

	private void validar(Horario horario) {
		if (horario != null) {
			if (horario.getRestaurante() != null && horario.getRestaurante().getId() > 0) {

				LocalTime horarioInicio = horario.getHorarioInicio();
				LocalTime horarioFim = horario.getHorarioFim();
				String diaDaSemana = horario.getDiaDaSemana();


				if (horarioInicio == null || horarioFim == null) {
					throw new IllegalArgumentException("Os horários de início e fim de expediente são obrigatórios.");
				}
				
				if (diaDaSemana == null) {
					throw new IllegalArgumentException("O dia da semana é obrigatório");
				}
				

				if (horarioInicio.isAfter(horarioFim)) {
					throw new IllegalArgumentException(
							"O horário de início deve ser anterior ao horário de fim de expediente.");
				}
				
				if (horarioInicio.equals(horarioFim)) {
					throw new IllegalArgumentException(
							"O horário de início é igual ao horário de fim.");
				}

			} else {
				throw new NullPointerException("O restaurante do horário é obrigatório.");
			}
			
		} else {
			throw new NullPointerException("O horário de atendimento não pode ser nulo.");
		}
	}
}
