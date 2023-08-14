package br.com.senai.core.domain;

import java.time.LocalTime;
import java.util.Objects;

public class Horario {

	private int id;
	private LocalTime horarioInicio;
	private LocalTime horarioFim;
	private String diaDaSemana;
	private Restaurante restaurante;
	
	public Horario(LocalTime horarioInicio, LocalTime horarioFim, String diaDaSemana, Restaurante restaurante) {
		this.horarioInicio = horarioInicio;
		this.horarioFim = horarioFim;
		this.diaDaSemana = diaDaSemana;
		this.restaurante = restaurante;
	}

	public Horario(int id, LocalTime horarioInicio, LocalTime horarioFim, String diaDaSemana, Restaurante restaurante) {
		this(horarioInicio, horarioFim, diaDaSemana, restaurante);
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public LocalTime getHorarioInicio() {
		return horarioInicio;
	}
	public void setHorarioInicio(LocalTime horarioInicio) {
		this.horarioInicio = horarioInicio;
	}

	public LocalTime getHorarioFim() {
		return horarioFim;
	}
	public void setHorarioFim(LocalTime horarioFim) {
		this.horarioFim = horarioFim;
	}

	public String getDiaDaSemana() {
		return diaDaSemana;
	}
	public void setDiaDaSemana(String diaDaSemana) {
		this.diaDaSemana = diaDaSemana;
	}
	
	public Restaurante getRestaurante() {
		return restaurante;
	}
	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Horario other = (Horario) obj;
		return id == other.id;
	}
}
