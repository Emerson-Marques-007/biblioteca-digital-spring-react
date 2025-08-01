package com.biblioteca.controller;

import com.biblioteca.dto.EmprestimoDTO;
import com.biblioteca.model.StatusEmprestimo;
import com.biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para operações relacionadas a Empréstimos.
 * 
 * Implementa conceitos de POO:
 * - Abstração: interface REST abstrai operações de negócio
 * - Encapsulamento: validações e tratamento de erros encapsulados
 * - Polimorfismo: diferentes comportamentos baseados no status
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@RestController
@RequestMapping("/api/emprestimos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    /**
     * Lista todos os empréstimos.
     * 
     * @return lista de empréstimos
     */
    @GetMapping
    public ResponseEntity<List<EmprestimoDTO>> listarTodos() {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarTodos();
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca um empréstimo por ID.
     * 
     * @param id ID do empréstimo
     * @return empréstimo encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoDTO> buscarPorId(@PathVariable Long id) {
        try {
            EmprestimoDTO emprestimo = emprestimoService.buscarPorId(id);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria um novo empréstimo.
     * 
     * @param emprestimoDTO dados do empréstimo
     * @return empréstimo criado
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody EmprestimoDTO emprestimoDTO) {
        try {
            EmprestimoDTO emprestimoCriado = emprestimoService.criar(emprestimoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Finaliza um empréstimo (devolução).
     * 
     * @param id ID do empréstimo
     * @return empréstimo finalizado
     */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        try {
            EmprestimoDTO emprestimoFinalizado = emprestimoService.finalizar(id);
            return ResponseEntity.ok(emprestimoFinalizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Renova um empréstimo.
     * 
     * @param id ID do empréstimo
     * @param request dados da renovação
     * @return empréstimo renovado
     */
    @PutMapping("/{id}/renovar")
    public ResponseEntity<?> renovar(@PathVariable Long id, @RequestBody RenovacaoRequest request) {
        try {
            int diasAdicionais = request.getDiasAdicionais() > 0 ? request.getDiasAdicionais() : 14;
            EmprestimoDTO emprestimoRenovado = emprestimoService.renovar(id, diasAdicionais);
            return ResponseEntity.ok(emprestimoRenovado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Lista empréstimos por status.
     * 
     * @param status status do empréstimo
     * @return lista de empréstimos
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmprestimoDTO>> listarPorStatus(@PathVariable String status) {
        try {
            StatusEmprestimo statusEnum = StatusEmprestimo.valueOf(status.toUpperCase());
            List<EmprestimoDTO> emprestimos = emprestimoService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(emprestimos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista empréstimos ativos.
     * 
     * @return lista de empréstimos ativos
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<EmprestimoDTO>> listarAtivos() {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarAtivos();
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista empréstimos atrasados.
     * 
     * @return lista de empréstimos atrasados
     */
    @GetMapping("/atrasados")
    public ResponseEntity<List<EmprestimoDTO>> listarAtrasados() {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarAtrasados();
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista empréstimos de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de empréstimos do usuário
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EmprestimoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista empréstimos ativos de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de empréstimos ativos do usuário
     */
    @GetMapping("/usuario/{usuarioId}/ativos")
    public ResponseEntity<List<EmprestimoDTO>> listarAtivosPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarAtivosPorUsuario(usuarioId);
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista empréstimos que vencem hoje.
     * 
     * @return lista de empréstimos que vencem hoje
     */
    @GetMapping("/vencem-hoje")
    public ResponseEntity<List<EmprestimoDTO>> listarQueVencemHoje() {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarQueVencemHoje();
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista empréstimos que vencem nos próximos dias.
     * 
     * @param dias número de dias
     * @return lista de empréstimos
     */
    @GetMapping("/vencem-em/{dias}")
    public ResponseEntity<List<EmprestimoDTO>> listarQueVencemEm(@PathVariable int dias) {
        try {
            List<EmprestimoDTO> emprestimos = emprestimoService.listarQueVencemEm(dias);
            return ResponseEntity.ok(emprestimos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza status de empréstimos vencidos.
     * 
     * @return número de empréstimos atualizados
     */
    @PostMapping("/atualizar-vencidos")
    public ResponseEntity<AtualizacaoResponse> atualizarEmprestimosVencidos() {
        try {
            int atualizados = emprestimoService.atualizarEmprestimosVencidos();
            return ResponseEntity.ok(new AtualizacaoResponse(atualizados, "Empréstimos atualizados com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AtualizacaoResponse(0, "Erro ao atualizar empréstimos"));
        }
    }

    /**
     * Obtém estatísticas de empréstimos.
     * 
     * @return estatísticas
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<EmprestimoService.EmprestimoEstatisticasDTO> obterEstatisticas() {
        try {
            EmprestimoService.EmprestimoEstatisticasDTO stats = emprestimoService.obterEstatisticas();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Classes auxiliares para requisições e respostas
    public static class RenovacaoRequest {
        private int diasAdicionais = 14;
        
        public int getDiasAdicionais() { return diasAdicionais; }
        public void setDiasAdicionais(int diasAdicionais) { this.diasAdicionais = diasAdicionais; }
    }

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

    public static class AtualizacaoResponse {
        private int emprestimosAtualizados;
        private String message;
        
        public AtualizacaoResponse(int emprestimosAtualizados, String message) {
            this.emprestimosAtualizados = emprestimosAtualizados;
            this.message = message;
        }
        
        public int getEmprestimosAtualizados() { return emprestimosAtualizados; }
        public void setEmprestimosAtualizados(int emprestimosAtualizados) { this.emprestimosAtualizados = emprestimosAtualizados; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

