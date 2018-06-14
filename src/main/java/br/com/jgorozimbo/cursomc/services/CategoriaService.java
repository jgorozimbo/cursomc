package br.com.jgorozimbo.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.jgorozimbo.cursomc.domain.Categoria;
import br.com.jgorozimbo.cursomc.repositories.CategoriaRepository;
import br.com.jgorozimbo.cursomc.services.expection.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;
	public Categoria buscar(Integer id) {
		
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + ", Tipo: " + Categoria.class.getName()));
	}
}
