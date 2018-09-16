package br.com.jgorozimbo.cursomc.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.jgorozimbo.cursomc.domain.Cidade;
import br.com.jgorozimbo.cursomc.domain.Cliente;
import br.com.jgorozimbo.cursomc.domain.Endereco;
import br.com.jgorozimbo.cursomc.domain.enums.Perfil;
import br.com.jgorozimbo.cursomc.domain.enums.TipoCliente;
import br.com.jgorozimbo.cursomc.dto.ClienteDTO;
import br.com.jgorozimbo.cursomc.dto.ClienteNewDTO;
import br.com.jgorozimbo.cursomc.repositories.ClienteRepository;
import br.com.jgorozimbo.cursomc.repositories.EnderecoRepository;
import br.com.jgorozimbo.cursomc.security.UserSS;
import br.com.jgorozimbo.cursomc.services.exception.AuthorizationException;
import br.com.jgorozimbo.cursomc.services.exception.DataIntegrityException;
import br.com.jgorozimbo.cursomc.services.exception.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private S3Service s3Service;
	
	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user == null || (!user.hasRole(Perfil.ADMIN) && !id.equals(user.getId()))) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update (Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public void delete (Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados");
		}
	}
	
	public List<Cliente> findAll () {
		return repo.findAll();
	}
	
	public Page<Cliente> findPage (Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}
	
	public Cliente fromDTO (ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()),pe.encode(objDto.getSenha()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if (objDto.getTelefone2()!=null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		if (objDto.getTelefone3()!=null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		return cli;
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {

		UserSS user = UserService.authenticated();
		if (user==null) {
			throw new AuthorizationException("Acesso negado");
		}
		URI uri = s3Service.uploadFile(multipartFile);
		Cliente cli = find(user.getId());
		cli.setImageUrl(uri.toString());
		repo.save(cli);
		
		return uri;
	}
}
