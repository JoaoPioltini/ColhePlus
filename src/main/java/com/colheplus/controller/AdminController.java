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
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin/usuarios")
    public List<Usuario> listarUsuarios(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return adminService.listarUsuarios(authorization);
    }

    @GetMapping("/admin/lotes")
    public List<Lote> listarLotes(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return adminService.listarLotes(authorization);
    }

    @DeleteMapping("/admin/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirUsuario(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminService.excluirUsuario(id, authorization);
    }

    @PatchMapping("/admin/lotes/{id}/desativar")
    public Lote desativarLote(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return adminService.desativarLote(id, authorization);
    }
}
