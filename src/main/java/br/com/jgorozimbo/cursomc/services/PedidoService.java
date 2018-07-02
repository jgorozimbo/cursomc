package br.com.jgorozimbo.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.jgorozimbo.cursomc.domain.Pedido;
import br.com.jgorozimbo.cursomc.repositories.PedidoRepository;
import br.com.jgorozimbo.cursomc.services.expection.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;
	public Pedido buscar(Integer id) {
		
		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + ", Tipo: " + Pedido.class.getName()));
	}
}