package br.com.senai.view.horario;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import br.com.senai.core.domain.Horario;
import br.com.senai.core.domain.Restaurante;
import br.com.senai.core.service.HorarioService;
import br.com.senai.core.service.RestauranteService;

public class ViewCadastroHorario {

	public class ViewCadastroHorarioAtendimento extends JFrame {

		private static final long serialVersionUID = 1L;

		private JPanel contentPane;
		private JFormattedTextField ftfInicio;
		private JFormattedTextField ftfFim;
		private JLabel lblFim;
		private JTable tableHorario;
		private JComboBox<String> cbDiaDaSemana;
		private HorarioService horarioService;
		private RestauranteService restauranteService;
		private JComboBox<Restaurante> cbRestaurante;
		private Horario horario;
		
		String[] diasSemana = { null, "DOMINGO", "SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO" };

		public void carregarCbSemana() {
			for (String dia : diasSemana) {
				cbDiaDaSemana.addItem(dia);
			}
		}
	
		public void carregarCbRestaurante() {
			List<Restaurante> restaurantes = restauranteService.listarTodos();
			for (Restaurante re : restaurantes) {
				cbRestaurante.addItem(re);
			}
		}
		
		public ViewCadastroHorarioAtendimento() {
			this.horarioService = new HorarioService();
			setResizable(false);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 682, 428);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblRestaurante = new JLabel("Restaurante");
			lblRestaurante.setBounds(10, 11, 78, 14);
			contentPane.add(lblRestaurante);

			cbRestaurante = new JComboBox<Restaurante>();
			cbRestaurante.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    Restaurante restauranteSelecionado = (Restaurante) cbRestaurante.getSelectedItem();
				    if (restauranteSelecionado != null) {
				        List<Horario> horariosAtendimento = horarioService.listarPor(restauranteSelecionado);
				        HorarioTableModel model = new HorarioTableModel(horariosAtendimento);
				        tableHorario.setModel(model);
				        tableHorario.updateUI();
				        clearFieldsWithoutRestaurante();
				   }
				}
			});
			cbRestaurante.setBounds(98, 6, 558, 25);
			contentPane.add(cbRestaurante);

			JLabel lblDiaDaSemana = new JLabel("Dia da Semana");
			lblDiaDaSemana.setBounds(10, 45, 91, 16);
			contentPane.add(lblDiaDaSemana);

			cbDiaDaSemana = new JComboBox<String>();
			cbDiaDaSemana.setBounds(108, 42, 119, 25);
			contentPane.add(cbDiaDaSemana);

			JButton btnAdicionar = new JButton("Adicionar");
			btnAdicionar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String diaDaSemana = (String) cbDiaDaSemana.getSelectedItem();
						String aberturaStr = ftfInicio.getText();
						String fechamentoStr = ftfFim.getText();
						Restaurante restaurante = (Restaurante) cbRestaurante.getSelectedItem();

						if (aberturaStr.isEmpty() || fechamentoStr.isEmpty()) {
							JOptionPane.showMessageDialog(contentPane, "Todos os campos são obrigatórios!");
						} else {
							LocalTime horarioInicio = LocalTime.parse(inicioStr);
							LocalTime horarioFim = LocalTime.parse(fimStr);

							if (horario == null) {
								horario = new Horario(horarioInicio,
										horarioFim, diaDaSemana, restaurante);
								horarioService.salvar(horario);
								JOptionPane.showMessageDialog(contentPane, "Horário de atendimento inserido com sucesso!");
								clearFields();
								horario = null;
							} else {
								horario.setDiaDaSemana(diaDaSemana);
								horario.setHorarioInicio(horarioInicio);
								horario.setHorarioFim(horarioFim);
								horario.setRestaurante(restaurante);
								horarioService.salvar(horario);
								JOptionPane.showMessageDialog(contentPane, "Horário de atendimento alterado com sucesso!");
								clearFields();
								horario = null;
							}

						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(contentPane, ex.getMessage());
						if (horarioAtendimento.getId() <= 0) {
							horarioAtendimento = null;
						}
					}

				}
			});
			btnAdicionar.setBounds(558, 42, 98, 26);
			contentPane.add(btnAdicionar);

			ftfFim = new JFormattedTextField();
			ftfFim.setBounds(470, 43, 78, 20);
			contentPane.add(ftfFim);

			JLabel lblAbertura = new JLabel("Abertura"); 
			lblAbertura.setBounds(237, 45, 55, 16);
			contentPane.add(lblAbertura);

			ftfInicio = new JFormattedTextField();
			ftfInicio.setBounds(302, 42, 71, 20);
			contentPane.add(ftfInicio);

			lblFim = new JLabel("Fechamento");
			lblFim.setBounds(383, 45, 77, 16);
			contentPane.add(lblFim);

			JLabel lblHorarios = new JLabel("Horários");
			lblHorarios.setBounds(10, 72, 55, 16);
			contentPane.add(lblHorarios);

			JButton btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearFields();
				}
			});
			btnCancelar.setBounds(558, 351, 98, 26);
			contentPane.add(btnCancelar);

			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(null, "Ações", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.setBounds(430, 131, 226, 120);
			contentPane.add(panel);
			panel.setLayout(null);

			JButton btnEditar = new JButton("Editar");
			btnEditar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int linhaSelecionada = tableHorario.getSelectedRow();
					HorarioTableModel model = (HorarioTableModel) tableHorario.getModel();
					if (linhaSelecionada >= 0) {
						Horario horarioAtendimentoSelecionado = model.getPor(linhaSelecionada);
						setHorarioAtendimento(horarioAtendimentoSelecionado);
					} else {
						JOptionPane.showMessageDialog(contentPane, "Selecione uma linha para edição.");
					}
				}
			});
			btnEditar.setBounds(12, 30, 202, 26);
			panel.add(btnEditar);

			JButton btnExcluir = new JButton("Excluir");
			btnExcluir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int linhaSelecionada = tableHorario.getSelectedRow();
					if (linhaSelecionada >= 0) {
						int opcao = JOptionPane.showConfirmDialog(contentPane, "Deseja realmente remover?", "Remoção",
								JOptionPane.YES_NO_OPTION);
						if (opcao == 0) {
							HorarioTableModel model = (HorarioAtendimentoTableModel) tableHorario.getModel();
							Horario horarioAtendimentoSelecionado = model.getPor(linhaSelecionada);
							try {
								horarioService.removerPor(horarioAtendimentoSelecionado.getId());
								List<Horario> horariosAtendimentoRestantes = horarioService.listarPor((Restaurante) cbRestaurante.getSelectedItem());
								model = new HorarioTableModel(horariosAtendimentoRestantes);
								tableHorario.setModel(model);	
								JOptionPane.showMessageDialog(contentPane, "Horário de atendimento removido com sucesso!");
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(contentPane, ex.getMessage());
							}
							tableHorario.clearSelection();
						}
					} else {
						JOptionPane.showMessageDialog(contentPane, "Selecione uma linha para remoção.");
					}
				}
			});
			btnExcluir.setBounds(12, 68, 202, 26);
			panel.add(btnExcluir);

			JLabel lblLinha = new JLabel(
					"____________________________________________________________________________________________");
			lblLinha.setBounds(10, 78, 646, 16);
			contentPane.add(lblLinha);

			tableHorario = new JTable(new HorarioTableModel(new ArrayList<Horario>()));
			this.configurarTabela();
			JScrollPane scrollPane = new JScrollPane(tableHorario);
			scrollPane.setBounds(10, 114, 399, 203);
			contentPane.add(scrollPane);

			this.restauranteService = new RestauranteService();
			this.carregarCbRestaurante();
			this.carregarComboSemana();
		    mascara(ftfInicio, "##:##");
		    mascara(ftfFim, "##:##");
		}
		
		private void mascara(JFormattedTextField field, String mask) {
		    try {
		        MaskFormatter maskFormatter = new MaskFormatter(mask);
		        maskFormatter.setPlaceholderCharacter('0');
		        maskFormatter.install(field);
		    } catch (ParseException ex) {
		        ex.printStackTrace();
		    }
		}
		
	    private void clearFields() {
	        cbDiaDaSemana.setSelectedItem(null);
	        ftfInicio.setText("");
	        ftfFim.setText("");
	        cbRestaurante.setSelectedIndex(0);
	        horario = null;
	    }
	    
	    private void clearFieldsWithoutRestaurante() {
	        cbDiaDaSemana.setSelectedItem(null);
	        ftfInicio.setText("");
	        ftfFim.setText("");
	        horario = null;
	    }
	    
		private void setHorarioAtendimento(Horario horario) {
			this.horario = horario;
			cbRestaurante.setSelectedItem(horario.getRestaurante());
			ftfInicio.setValue(horario.getHorarioInicio());
			ftfFim.setValue(horario.getHorarioFim());
			cbDiaDaSemana.setSelectedItem(horario.getDiaDaSemana());
		}
		
		private void configurarColuna(int indice, int largura) {
			this.tableHorario.getColumnModel().getColumn(indice).setResizable(false);
			this.tableHorario.getColumnModel().getColumn(indice).setPreferredWidth(largura);
		}
		
		private void configurarTabela() {
			final int COLUNA_ID = 0;
			final int COLUNA_NOME = 1;
			this.tableHorario.getTableHeader().setReorderingAllowed(false);
			this.tableHorario.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.configurarColuna(COLUNA_ID, 50);
			this.configurarColuna(COLUNA_NOME, 550);
		}
		
	}
}
