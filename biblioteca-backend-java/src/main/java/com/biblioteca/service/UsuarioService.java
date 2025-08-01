package com.biblioteca.service;

import com.biblioteca.dto.UsuarioDTO;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para operações de negócio relacionadas a Usuários.
 * 
 * Implementa conceitos de POO:
 * - Encapsulamento: lógica de negócio encapsulada
 * - Abstração: interface de serviço abstrai complexidade
 * - Separação de responsabilidades
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lista todos os usuários.
     * 
     * @return lista de DTOs de usuários
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário por ID.
     * 
     * @param id ID do usuário
     * @return DTO do usuário se encontrado
     * @throws RuntimeException se não encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return modelMapper.map(usuario.get(), UsuarioDTO.class);
        }
        throw new RuntimeException("Usuário não encontrado com ID: " + id);
    }

    /**
     * Busca um usuário por email.
     * 
     * @param email email do usuário
     * @return DTO do usuário se encontrado
     * @throws RuntimeException se não encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            return modelMapper.map(usuario.get(), UsuarioDTO.class);
        }
        throw new RuntimeException("Usuário não encontrado com email: " + email);
    }

    /**
     * Cria um novo usuário.
     * 
     * @param usuarioDTO dados do usuário
     * @return DTO do usuário criado
     * @throws RuntimeException se email já existe
     */
    public UsuarioDTO criar(UsuarioDTO usuarioDTO) {
        // Validar se email já existe
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Já existe um usuário com o email: " + usuarioDTO.getEmail());
        }

        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        
        // Garantir que o usuário seja criado como ativo
        usuario.setStatusAtivo(true);
        
        // Validar email
        if (!usuario.validarEmail()) {
            throw new RuntimeException("Email inválido: " + usuario.getEmail());
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioSalvo, UsuarioDTO.class);
    }

    /**
     * Atualiza um usuário existente.
     * 
     * @param id ID do usuário
     * @param usuarioDTO dados atualizados
     * @return DTO do usuário atualizado
     * @throws RuntimeException se não encontrado ou email duplicado
     */
    public UsuarioDTO atualizar(Long id, UsuarioDTO usuarioDTO) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (!usuarioExistente.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        Usuario usuario = usuarioExistente.get();
        
        // Verificar se o email foi alterado e se já existe
        if (!usuario.getEmail().equals(usuarioDTO.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new RuntimeException("Já existe um usuário com o email: " + usuarioDTO.getEmail());
            }
        }

        // Atualizar campos
        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setEndereco(usuarioDTO.getEndereco());
        
        // Validar email
        if (!usuario.validarEmail()) {
            throw new RuntimeException("Email inválido: " + usuario.getEmail());
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioSalvo, UsuarioDTO.class);
    }

    /**
     * Remove um usuário.
     * 
     * @param id ID do usuário
     * @throws RuntimeException se não encontrado ou tem empréstimos ativos
     */
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        // Verificar se pode ser deletado (não tem empréstimos ativos)
        if (!usuarioRepository.podeSerDeletado(id)) {
            throw new RuntimeException("Não é possível deletar o usuário. Existem empréstimos ativos.");
        }

        usuarioRepository.deleteById(id);
    }

    /**
     * Ativa um usuário.
     * 
     * @param id ID do usuário
     * @return DTO do usuário ativado
     * @throws RuntimeException se não encontrado
     */
    public UsuarioDTO ativar(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();
        usuario.ativar();
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioSalvo, UsuarioDTO.class);
    }

    /**
     * Desativa um usuário.
     * 
     * @param id ID do usuário
     * @return DTO do usuário desativado
     * @throws RuntimeException se não encontrado ou tem empréstimos ativos
     */
    public UsuarioDTO desativar(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        // Verificar se tem empréstimos ativos
        long emprestimosAtivos = usuarioRepository.contarEmprestimosAtivos(id);
        if (emprestimosAtivos > 0) {
            throw new RuntimeException("Não é possível desativar o usuário. Existem empréstimos ativos.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.desativar();
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioSalvo, UsuarioDTO.class);
    }

    /**
     * Busca usuários por termo geral (nome ou email).
     * 
     * @param termo termo de busca
     * @return lista de DTOs de usuários encontrados
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarPorTermoGeral(String termo) {
        List<Usuario> usuarios = usuarioRepository.buscarPorTermoGeral(termo);
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lista usuários ativos.
     * 
     * @return lista de DTOs de usuários ativos
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarAtivos() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosAtivos();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lista usuários inativos.
     * 
     * @return lista de DTOs de usuários inativos
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarInativos() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosInativos();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lista usuários com empréstimos ativos.
     * 
     * @return lista de DTOs de usuários com empréstimos ativos
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarComEmprestimosAtivos() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosComEmprestimosAtivos();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lista usuários com empréstimos em atraso.
     * 
     * @return lista de DTOs de usuários com empréstimos atrasados
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarComEmprestimosAtrasados() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosComEmprestimosAtrasados();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Conta quantos usuários estão ativos.
     * 
     * @return número de usuários ativos
     */
    @Transactional(readOnly = true)
    public long contarAtivos() {
        return usuarioRepository.contarUsuariosAtivos();
    }

    /**
     * Conta quantos usuários estão inativos.
     * 
     * @return número de usuários inativos
     */
    @Transactional(readOnly = true)
    public long contarInativos() {
        return usuarioRepository.contarUsuariosInativos();
    }

    /**
     * Conta quantos empréstimos ativos um usuário possui.
     * 
     * @param id ID do usuário
     * @return número de empréstimos ativos
     */
    @Transactional(readOnly = true)
    public long contarEmprestimosAtivos(Long id) {
        return usuarioRepository.contarEmprestimosAtivos(id);
    }

    /**
     * Verifica se um usuário pode fazer empréstimos.
     * 
     * @param id ID do usuário
     * @return true se pode emprestar
     */
    @Transactional(readOnly = true)
    public boolean podeEmprestar(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(Usuario::podeEmprestar).orElse(false);
    }

    /**
     * Verifica se um usuário está ativo.
     * 
     * @param id ID do usuário
     * @return true se ativo
     */
    @Transactional(readOnly = true)
    public boolean isAtivo(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(Usuario::isAtivo).orElse(false);
    }
}

