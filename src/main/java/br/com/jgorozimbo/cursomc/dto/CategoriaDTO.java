package br.com.jgorozimbo.cursomc.dto;

import java.io.Serializable;

import br.com.jgorozimbo.cursomc.domain.Categoria;

public class CategoriaDTO implements Serializable{
	private static final long serialVersionUID = 1L;

	Integer id;
	String nome;
	
	public CategoriaDTO () {
	}

	public CategoriaDTO (Categoria categoria) {
		id = categoria.getId();
		nome = categoria.getNome();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
