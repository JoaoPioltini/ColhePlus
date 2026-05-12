package com.colheplus.controller;

import com.colheplus.model.Lote;
import com.colheplus.model.Usuario;
import com.colheplus.service.AdminService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin/usuarios")
    public List<Usuario> listarUsuarios() {
        return adminService.listarUsuarios();
    }

    @DeleteMapping("/admin/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirUsuario(@PathVariable Long id) {
        adminService.excluirUsuario(id);
    }

    @PatchMapping("/admin/lotes/{id}/desativar")
    public Lote desativarLote(@PathVariable Long id) {
        return adminService.desativarLote(id);
    }
}
