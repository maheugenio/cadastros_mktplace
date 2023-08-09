package br.com.senai;

import br.com.senai.core.dao.ManagerDb;

public class Principal {

	public static void main(String[] args) {
		
		ManagerDb.getInstance();
		System.out.println("Conectou");

	}

}
