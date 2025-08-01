package com.biblioteca.controller;

import com.biblioteca.dto.LivroDTO;
import com.biblioteca.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para operações relacionadas a Livros.
 * 
 * Implementa conceitos de POO:
 * - Abstração: interface REST abstrai operações de negócio
 * - Encapsulamento: validações e tratamento de erros encapsulados
 * - Separação de responsabilidades
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class LivroController {

    @Autowired
    private LivroService livroService;

    /**
     * Lista todos os livros.
     * 
     * @return lista de livros
     */
    @GetMapping
    public ResponseEntity<List<LivroDTO>> listarTodos() {
        try {
            List<LivroDTO> livros = livroService.listarTodos();
            return ResponseEntity.ok(livros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca um livro por ID.
     * 
     * @param id ID do livro
     * @return livro encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<LivroDTO> buscarPorId(@PathVariable Long id) {
        try {
            LivroDTO livro = livroService.buscarPorId(id);
            return ResponseEntity.ok(livro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria um novo livro.
     * 
     * @param livroDTO dados do livro
     * @return livro criado
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody LivroDTO livroDTO) {
        try {
            LivroDTO livroCriado = livroService.criar(livroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(livroCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Atualiza um livro existente.
     * 
     * @param id ID do livro
     * @param livroDTO dados atualizados
     * @return livro atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody LivroDTO livroDTO) {
        try {
            LivroDTO livroAtualizado = livroService.atualizar(id, livroDTO);
            return ResponseEntity.ok(livroAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Remove um livro.
     * 
     * @param id ID do livro
     * @return resposta de sucesso ou erro
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            livroService.deletar(id);
            return ResponseEntity.ok(new SuccessResponse("Livro deletado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Busca livros por termo geral.
     * 
     * @param termo termo de busca
     * @return lista de livros encontrados
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<LivroDTO>> buscarPorTermoGeral(@RequestParam String termo) {
        try {
            List<LivroDTO> livros = livroService.buscarPorTermoGeral(termo);
            return ResponseEntity.ok(livros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista livros disponíveis.
     * 
     * @return lista de livros disponíveis
     */
    @GetMapping("/disponiveis")
    public ResponseEntity<List<LivroDTO>> listarDisponiveis() {
        try {
            List<LivroDTO> livros = livroService.listarDisponiveis();
            return ResponseEntity.ok(livros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca livros por autor.
     * 
     * @param autor nome do autor
     * @return lista de livros do autor
     */
    @GetMapping("/autor")
    public ResponseEntity<List<LivroDTO>> buscarPorAutor(@RequestParam String autor) {
        try {
            List<LivroDTO> livros = livroService.buscarPorAutor(autor);
            return ResponseEntity.ok(livros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca livros por gênero.
     * 
     * @param genero gênero do livro
     * @return lista de livros do gênero
     */
    @GetMapping("/genero")
    public ResponseEntity<List<LivroDTO>> buscarPorGenero(@RequestParam String genero) {
        try {
            List<LivroDTO> livros = livroService.buscarPorGenero(genero);
            return ResponseEntity.ok(livros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca livros por ano de publicação.
     * 
     * @param ano ano de publicação
     * @return lista de livros do ano
     */
    @GetMapping("/ano")
    public ResponseEntity<List<LivroDTO>> buscarPorAno(@RequestParam Integer ano) {
        try {
            List<LivroDTO> livros = livroService.buscarPorAno(ano);
            return ResponseEntity.ok(livros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca livro por ISBN.
     * 
     * @param isbn ISBN do livro
     * @return livro encontrado
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<LivroDTO> buscarPorIsbn(@PathVariable String isbn) {
        try {
            LivroDTO livro = livroService.buscarPorIsbn(isbn);
            return ResponseEntity.ok(livro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtém estatísticas de livros.
     * 
     * @return estatísticas
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<LivroEstatisticasDTO> obterEstatisticas() {
        try {
            LivroEstatisticasDTO stats = new LivroEstatisticasDTO();
            stats.setTotalLivros(livroService.listarTodos().size());
            stats.setLivrosDisponiveis(livroService.contarDisponiveis());
            stats.setLivrosEmprestados(livroService.contarEmprestados());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verifica se um livro está disponível.
     * 
     * @param id ID do livro
     * @return status de disponibilidade
     */
    @GetMapping("/{id}/disponivel")
    public ResponseEntity<DisponibilidadeDTO> verificarDisponibilidade(@PathVariable Long id) {
        try {
            boolean disponivel = livroService.isDisponivel(id);
            return ResponseEntity.ok(new DisponibilidadeDTO(disponivel));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Classes auxiliares para respostas
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class LivroEstatisticasDTO {
        private long totalLivros;
        private long livrosDisponiveis;
        private long livrosEmprestados;
        
        // Getters e Setters
        public long getTotalLivros() { return totalLivros; }
        public void setTotalLivros(long totalLivros) { this.totalLivros = totalLivros; }
        
        public long getLivrosDisponiveis() { return livrosDisponiveis; }
        public void setLivrosDisponiveis(long livrosDisponiveis) { this.livrosDisponiveis = livrosDisponiveis; }
        
        public long getLivrosEmprestados() { return livrosEmprestados; }
        public void setLivrosEmprestados(long livrosEmprestados) { this.livrosEmprestados = livrosEmprestados; }
    }

    public static class DisponibilidadeDTO {
        private boolean disponivel;
        
        public DisponibilidadeDTO(boolean disponivel) {
            this.disponivel = disponivel;
        }
        
        public boolean isDisponivel() { return disponivel; }
        public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    }
}

