package com.biblioteca.controller;

import com.biblioteca.dto.UsuarioDTO;
import com.biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para operações relacionadas a Usuários.
 * 
 * Implementa conceitos de POO:
 * - Abstração: interface REST abstrai operações de negócio
 * - Encapsulamento: validações e tratamento de erros encapsulados
 * - Separação de responsabilidades
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários.
     * 
     * @return lista de usuários
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarTodos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca um usuário por ID.
     * 
     * @param id ID do usuário
     * @return usuário encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria um novo usuário.
     * 
     * @param usuarioDTO dados do usuário
     * @return usuário criado
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Atualiza um usuário existente.
     * 
     * @param id ID do usuário
     * @param usuarioDTO dados atualizados
     * @return usuário atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO usuarioAtualizado = usuarioService.atualizar(id, usuarioDTO);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Remove um usuário.
     * 
     * @param id ID do usuário
     * @return resposta de sucesso ou erro
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.ok(new SuccessResponse("Usuário deletado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Ativa um usuário.
     * 
     * @param id ID do usuário
     * @return usuário ativado
     */
    @PutMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            UsuarioDTO usuarioAtivado = usuarioService.ativar(id);
            return ResponseEntity.ok(usuarioAtivado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Desativa um usuário.
     * 
     * @param id ID do usuário
     * @return usuário desativado
     */
    @PutMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            UsuarioDTO usuarioDesativado = usuarioService.desativar(id);
            return ResponseEntity.ok(usuarioDesativado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    /**
     * Busca usuários por termo geral.
     * 
     * @param termo termo de busca
     * @return lista de usuários encontrados
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarPorTermoGeral(@RequestParam String termo) {
        try {
            List<UsuarioDTO> usuarios = usuarioService.buscarPorTermoGeral(termo);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista usuários ativos.
     * 
     * @return lista de usuários ativos
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<UsuarioDTO>> listarAtivos() {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarAtivos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista usuários inativos.
     * 
     * @return lista de usuários inativos
     */
    @GetMapping("/inativos")
    public ResponseEntity<List<UsuarioDTO>> listarInativos() {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarInativos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista usuários com empréstimos ativos.
     * 
     * @return lista de usuários com empréstimos ativos
     */
    @GetMapping("/com-emprestimos-ativos")
    public ResponseEntity<List<UsuarioDTO>> listarComEmprestimosAtivos() {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarComEmprestimosAtivos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista usuários com empréstimos em atraso.
     * 
     * @return lista de usuários com empréstimos atrasados
     */
    @GetMapping("/com-emprestimos-atrasados")
    public ResponseEntity<List<UsuarioDTO>> listarComEmprestimosAtrasados() {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarComEmprestimosAtrasados();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca usuário por email.
     * 
     * @param email email do usuário
     * @return usuário encontrado
     */
    @GetMapping("/email")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam String email) {
        try {
            UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtém estatísticas de usuários.
     * 
     * @return estatísticas
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<UsuarioEstatisticasDTO> obterEstatisticas() {
        try {
            UsuarioEstatisticasDTO stats = new UsuarioEstatisticasDTO();
            stats.setTotalUsuarios(usuarioService.listarTodos().size());
            stats.setUsuariosAtivos(usuarioService.contarAtivos());
            stats.setUsuariosInativos(usuarioService.contarInativos());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verifica se um usuário pode fazer empréstimos.
     * 
     * @param id ID do usuário
     * @return status de permissão para empréstimo
     */
    @GetMapping("/{id}/pode-emprestar")
    public ResponseEntity<PodeEmprestarDTO> verificarPodeEmprestar(@PathVariable Long id) {
        try {
            boolean podeEmprestar = usuarioService.podeEmprestar(id);
            long emprestimosAtivos = usuarioService.contarEmprestimosAtivos(id);
            
            return ResponseEntity.ok(new PodeEmprestarDTO(podeEmprestar, emprestimosAtivos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Conta empréstimos ativos de um usuário.
     * 
     * @param id ID do usuário
     * @return número de empréstimos ativos
     */
    @GetMapping("/{id}/emprestimos-ativos/count")
    public ResponseEntity<EmprestimosAtivosDTO> contarEmprestimosAtivos(@PathVariable Long id) {
        try {
            long count = usuarioService.contarEmprestimosAtivos(id);
            return ResponseEntity.ok(new EmprestimosAtivosDTO(count));
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

    public static class UsuarioEstatisticasDTO {
        private long totalUsuarios;
        private long usuariosAtivos;
        private long usuariosInativos;
        
        // Getters e Setters
        public long getTotalUsuarios() { return totalUsuarios; }
        public void setTotalUsuarios(long totalUsuarios) { this.totalUsuarios = totalUsuarios; }
        
        public long getUsuariosAtivos() { return usuariosAtivos; }
        public void setUsuariosAtivos(long usuariosAtivos) { this.usuariosAtivos = usuariosAtivos; }
        
        public long getUsuariosInativos() { return usuariosInativos; }
        public void setUsuariosInativos(long usuariosInativos) { this.usuariosInativos = usuariosInativos; }
    }

    public static class PodeEmprestarDTO {
        private boolean podeEmprestar;
        private long emprestimosAtivos;
        
        public PodeEmprestarDTO(boolean podeEmprestar, long emprestimosAtivos) {
            this.podeEmprestar = podeEmprestar;
            this.emprestimosAtivos = emprestimosAtivos;
        }
        
        public boolean isPodeEmprestar() { return podeEmprestar; }
        public void setPodeEmprestar(boolean podeEmprestar) { this.podeEmprestar = podeEmprestar; }
        
        public long getEmprestimosAtivos() { return emprestimosAtivos; }
        public void setEmprestimosAtivos(long emprestimosAtivos) { this.emprestimosAtivos = emprestimosAtivos; }
    }

    public static class EmprestimosAtivosDTO {
        private long count;
        
        public EmprestimosAtivosDTO(long count) {
            this.count = count;
        }
        
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}

