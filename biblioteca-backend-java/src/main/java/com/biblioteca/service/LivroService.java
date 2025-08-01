package com.biblioteca.service;

import com.biblioteca.dto.LivroDTO;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para operações de negócio relacionadas a Livros.
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
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lista todos os livros.
     * 
     * @return lista de DTOs de livros
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> listarTodos() {
        List<Livro> livros = livroRepository.findAll();
        return livros.stream()
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Busca um livro por ID.
     * 
     * @param id ID do livro
     * @return DTO do livro se encontrado
     * @throws RuntimeException se não encontrado
     */
    @Transactional(readOnly = true)
    public LivroDTO buscarPorId(Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        if (livro.isPresent()) {
            return modelMapper.map(livro.get(), LivroDTO.class);
        }
        throw new RuntimeException("Livro não encontrado com ID: " + id);
    }

    /**
     * Busca um livro por ISBN.
     * 
     * @param isbn ISBN do livro
     * @return DTO do livro se encontrado
     * @throws RuntimeException se não encontrado
     */
    @Transactional(readOnly = true)
    public LivroDTO buscarPorIsbn(String isbn) {
        Optional<Livro> livro = livroRepository.findByIsbn(isbn);
        if (livro.isPresent()) {
            return modelMapper.map(livro.get(), LivroDTO.class);
        }
        throw new RuntimeException("Livro não encontrado com ISBN: " + isbn);
    }

    /**
     * Cria um novo livro.
     * 
     * @param livroDTO dados do livro
     * @return DTO do livro criado
     * @throws RuntimeException se ISBN já existe
     */
    public LivroDTO criar(LivroDTO livroDTO) {
        // Validar se ISBN já existe
        if (livroRepository.existsByIsbn(livroDTO.getIsbn())) {
            throw new RuntimeException("Já existe um livro com o ISBN: " + livroDTO.getIsbn());
        }

        Livro livro = modelMapper.map(livroDTO, Livro.class);
        
        // Garantir que o livro seja criado como disponível
        livro.setStatusDisponibilidade(true);
        
        // Validar ISBN
        if (!livro.validarIsbn()) {
            throw new RuntimeException("ISBN inválido: " + livro.getIsbn());
        }

        Livro livroSalvo = livroRepository.save(livro);
        return modelMapper.map(livroSalvo, LivroDTO.class);
    }

    /**
     * Atualiza um livro existente.
     * 
     * @param id ID do livro
     * @param livroDTO dados atualizados
     * @return DTO do livro atualizado
     * @throws RuntimeException se não encontrado ou ISBN duplicado
     */
    public LivroDTO atualizar(Long id, LivroDTO livroDTO) {
        Optional<Livro> livroExistente = livroRepository.findById(id);
        if (!livroExistente.isPresent()) {
            throw new RuntimeException("Livro não encontrado com ID: " + id);
        }

        Livro livro = livroExistente.get();
        
        // Verificar se o ISBN foi alterado e se já existe
        if (!livro.getIsbn().equals(livroDTO.getIsbn())) {
            if (livroRepository.existsByIsbn(livroDTO.getIsbn())) {
                throw new RuntimeException("Já existe um livro com o ISBN: " + livroDTO.getIsbn());
            }
        }

        // Atualizar campos
        livro.setTitulo(livroDTO.getTitulo());
        livro.setAutor(livroDTO.getAutor());
        livro.setIsbn(livroDTO.getIsbn());
        livro.setAnoPublicacao(livroDTO.getAnoPublicacao());
        livro.setGenero(livroDTO.getGenero());
        
        // Validar ISBN
        if (!livro.validarIsbn()) {
            throw new RuntimeException("ISBN inválido: " + livro.getIsbn());
        }

        Livro livroSalvo = livroRepository.save(livro);
        return modelMapper.map(livroSalvo, LivroDTO.class);
    }

    /**
     * Remove um livro.
     * 
     * @param id ID do livro
     * @throws RuntimeException se não encontrado ou tem empréstimos ativos
     */
    public void deletar(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado com ID: " + id);
        }

        // Verificar se pode ser deletado (não tem empréstimos ativos)
        if (!livroRepository.podeSerDeletado(id)) {
            throw new RuntimeException("Não é possível deletar o livro. Existem empréstimos ativos.");
        }

        livroRepository.deleteById(id);
    }

    /**
     * Busca livros por termo geral (título, autor ou ISBN).
     * 
     * @param termo termo de busca
     * @return lista de DTOs de livros encontrados
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscarPorTermoGeral(String termo) {
        List<Livro> livros = livroRepository.buscarPorTermoGeral(termo);
        return livros.stream()
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lista livros disponíveis para empréstimo.
     * 
     * @return lista de DTOs de livros disponíveis
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> listarDisponiveis() {
        List<Livro> livros = livroRepository.findLivrosDisponiveis();
        return livros.stream()
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Busca livros por autor.
     * 
     * @param autor nome do autor
     * @return lista de DTOs de livros do autor
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscarPorAutor(String autor) {
        List<Livro> livros = livroRepository.findByAutorContainingIgnoreCase(autor);
        return livros.stream()
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Busca livros por gênero.
     * 
     * @param genero gênero do livro
     * @return lista de DTOs de livros do gênero
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscarPorGenero(String genero) {
        List<Livro> livros = livroRepository.findByGeneroContainingIgnoreCase(genero);
        return livros.stream()
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Busca livros por ano de publicação.
     * 
     * @param ano ano de publicação
     * @return lista de DTOs de livros do ano
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscarPorAno(Integer ano) {
        List<Livro> livros = livroRepository.findByAnoPublicacao(ano);
        return livros.stream()
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Conta quantos livros estão disponíveis.
     * 
     * @return número de livros disponíveis
     */
    @Transactional(readOnly = true)
    public long contarDisponiveis() {
        return livroRepository.contarLivrosDisponiveis();
    }

    /**
     * Conta quantos livros estão emprestados.
     * 
     * @return número de livros emprestados
     */
    @Transactional(readOnly = true)
    public long contarEmprestados() {
        return livroRepository.contarLivrosEmprestados();
    }

    /**
     * Marca um livro como emprestado.
     * Método interno usado pelo serviço de empréstimos.
     * 
     * @param id ID do livro
     */
    public void marcarComoEmprestado(Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            livro.marcarComoEmprestado();
            livroRepository.save(livro);
        }
    }

    /**
     * Marca um livro como disponível.
     * Método interno usado pelo serviço de empréstimos.
     * 
     * @param id ID do livro
     */
    public void marcarComoDisponivel(Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            livro.marcarComoDisponivel();
            livroRepository.save(livro);
        }
    }

    /**
     * Verifica se um livro está disponível para empréstimo.
     * 
     * @param id ID do livro
     * @return true se disponível
     */
    @Transactional(readOnly = true)
    public boolean isDisponivel(Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.map(Livro::isDisponivel).orElse(false);
    }
}

