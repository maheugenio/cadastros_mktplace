package br.com.senai.core.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.senai.core.dao.DaoHorario;
import br.com.senai.core.dao.ManagerDb;
import br.com.senai.core.domain.Categoria;
import br.com.senai.core.domain.Endereco;
import br.com.senai.core.domain.Horario;
import br.com.senai.core.domain.Restaurante;

public class DaoPostgresHorario implements DaoHorario {
	
	private final String COUNT_BY_REST = "SELECT Count(*) qtde "
									   + "FROM horarios_atendimento h "
									   + "WHERE h.id_restaurante = ?";
	
	private final String INSERT = "INSERT INTO horarios_atendimento (dia_semana, hora_abertura, hora_fechamento, id_restaurante)"
								+ " VALUES (?, ?, ?, ?)";

	private final String UPDATE = "UPDATE horarios_atendimento SET dia_semana = ?, hora_abertura = ?, "
								+ "hora_fechamento = ?, id_restaurante = ?" + " WHERE id = ?";

	private final String DELETE = "DELETE FROM horarios_atendimento WHERE id = ?";

	private final String SELECT_BY_ID = "SELECT h.id, h.dia_semana, h.hora_abertura, h.hora_fechamento, r.id id_restaurante,"
				  + " r.nome nome_restaurante, r.descricao, r.cidade, r.logradouro, r.bairro, r.complemento," 
				  + " c.id id_categoria, c.nome nome_categoria"
				  + " FROM horarios_atendimento h, restaurantes r, categorias c " + " WHERE h.id_restaurante = r.id"
				  + " AND r.id_categoria = c.id" + " AND h.id = ?";

	private final String SELECT_BY_RESTAURANTE = "SELECT h.id, h.dia_semana, h.hora_abertura, h.hora_fechamento,"
			+ " r.id id_restaurante, r.nome nome_restaurante, r.descricao, r.cidade, r.logradouro, r.bairro, r.complemento," 
			+ " c.id id_categoria, c.nome nome_categoria FROM horarios_atendimento h, restaurantes r, categorias c " 
			+ " WHERE h.id_restaurante = r.id AND r.id_categoria = c.id" + " AND r.id = ?";
	
	private Connection conexao;
	
	public DaoPostgresHorario() {
		this.conexao = ManagerDb.getInstance().getConexao();
	}

	@Override
	public int contarPor(int idDoRestaurante) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conexao.prepareStatement(COUNT_BY_REST);
			ps.setInt(1, idDoRestaurante);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("qtde");
			}
			return 0;
			
		} catch(Exception e) {
			throw new RuntimeException("Ocorreu um erro ao contar os horários de atendimento. "
					+ "Motivo: " + e.getMessage());
			
		} finally {
			ManagerDb.getInstance().fechar(ps);
			ManagerDb.getInstance().fechar(rs);
		}
	}

	private Horario extrairDo(ResultSet rs) {
		try {
			int id = rs.getInt("id");
			LocalTime horarioInicio = rs.getTime("horario_inicio").toLocalTime();
			LocalTime horarioFim = rs.getTime("horario_fim").toLocalTime();
			String diaSemana = rs.getString("dia_semana");
			
			int idDoRestaurante = rs.getInt("id_restaurante");
			String nomeDoRestaurante = rs.getString("nome_restaurante");
			String descricao = rs.getString("descricao");
			String cidade = rs.getString("cidade");
			String logradouro = rs.getString("logradouro");
			String bairro = rs.getString("bairro");
			String complemento = rs.getString("complemento");
			
			int idDaCategoria = rs.getInt("id_categoria");
			String nomeDaCategoria = rs.getString("nome_categoria");
			
			Endereco endereco = new Endereco(cidade, logradouro, bairro, complemento);
			Categoria categoria = new Categoria(idDaCategoria, nomeDaCategoria);

			Restaurante restaurante = new Restaurante(idDoRestaurante, nomeDoRestaurante, descricao, endereco, categoria);

			return new Horario(id, horarioInicio, horarioFim, diaSemana, restaurante);
			
		} catch (Exception e) {
			throw new RuntimeException(
					"Ocorreu um erro ao extrair horário de atendimento. "
					+ "Motivo: " + e.getMessage());
		}
	}
	
	@Override
	public List<Horario> listarPor(Restaurante restaurante) {
		List<Horario> horariosAtendimento = new ArrayList<Horario>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conexao.prepareStatement(SELECT_BY_RESTAURANTE);
			ps.setInt(1, restaurante.getId());
			rs = ps.executeQuery();
			while (rs.next()) {
				horariosAtendimento.add(extrairDo(rs));
			}
			return horariosAtendimento;
		} catch (Exception e) {
			throw new RuntimeException(
					"Ocorreu um erro ao listar os horários de atendimento. "
					+ "Motivo: " + e.getMessage());
		} finally {
			ManagerDb.getInstance().fechar(ps);
			ManagerDb.getInstance().fechar(rs);
		}
	}
	
	public Horario buscarPor(int id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conexao.prepareStatement(SELECT_BY_ID);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				return extrairDo(rs);
			}
			return null;
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro ao "
					+ "buscar o restaurante. Motivo: " + e.getMessage());
		}finally {
			ManagerDb.getInstance().fechar(ps);
			ManagerDb.getInstance().fechar(rs);
		}
	}

		@Override
		public void inserir(Horario horario) {
			PreparedStatement ps = null;
			
			try {
				ps = conexao.prepareStatement(INSERT);
				ps.setTime(1, Time.valueOf(horario.getHorarioInicio()));
				ps.setTime(2, Time.valueOf(horario.getHorarioFim()));
				ps.setString(3, horario.getDiaDaSemana());
				ps.setInt(4, horario.getRestaurante().getId());
				ps.execute();
				
			} catch(Exception e) {
				throw new RuntimeException(
						"Ocorreu um erro ao inserir horário de atendimento. "
						+ "Motivo: " + e.getMessage());
			} finally {
				ManagerDb.getInstance().fechar(ps);
			}

	}

		@Override
		public void alterar(Horario horario) {
			PreparedStatement ps = null;
			try {
				ManagerDb.getInstance().configurarAutocommitDa(conexao, false);	
				ps = conexao.prepareStatement(UPDATE);
				ps.setInt(1, horario.getId());
				ps.setTime(2, Time.valueOf(horario.getHorarioInicio()));
				ps.setTime(3, Time.valueOf(horario.getHorarioFim()));
				ps.setString(4, horario.getDiaDaSemana());
				ps.setInt(5, horario.getRestaurante().getId());
				
				boolean isAlteracaoOK = ps.executeUpdate() == 1;
				
				if (isAlteracaoOK) {
					this.conexao.commit();
					
				}else {
					this.conexao.rollback();
				}
				ManagerDb.getInstance().configurarAutocommitDa(conexao, true);
				
			} catch(Exception e) {
				throw new RuntimeException(
						"Ocorreu um erro ao alterar horário de atendimento. "
						+ "Motivo: " + e.getMessage());
				
			} finally {
				ManagerDb.getInstance().fechar(ps);
			}
		}
	
		@Override
		public void excluirPor(int id) {
			PreparedStatement ps = null;
			
			try {
				ManagerDb.getInstance().configurarAutocommitDa(conexao, false);
				ps = conexao.prepareStatement(DELETE);
				ps.setInt(1, id);
				
				boolean isExclusaoOK = ps.executeUpdate() == 1;
				
				if (isExclusaoOK) {
					this.conexao.commit();				
				}else {
					this.conexao.rollback();
				}
				ManagerDb.getInstance().configurarAutocommitDa(conexao, true);
				
			}catch(Exception e) {
				throw new RuntimeException("Ocorreu um erro ao excluir horário de atendimento. "
						+ "Motivo: " + e.getMessage());
				
			}finally {
				ManagerDb.getInstance().fechar(ps);
			}
		}
	
}
