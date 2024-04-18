package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoController {
    @GetMapping("/addCarrinho")
    public void addCarrinho(@RequestParam int produtoId, @RequestParam String comando, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true); // Obtém a sessão do usuário ou cria uma nova se não existir

        var clienteId = session.getAttribute("logado");

        Map<Integer, Integer> carrinho = (Map<Integer, Integer>) session.getAttribute("carrinho_" + clienteId);
        if (carrinho == null) {
            carrinho = new HashMap<>();
        }

        if (comando.equals("add")) {
            carrinho.put(produtoId, carrinho.getOrDefault(produtoId, 0) + 1);
        } else if (comando.equals("remove")) {
            if (carrinho.containsKey(produtoId)) {
                int quantidade = carrinho.get(produtoId);
                if (quantidade > 1) {
                    carrinho.put(produtoId, quantidade - 1);
                } else {
                    carrinho.remove(produtoId);
                }
            }
        }

        session.setAttribute("carrinho_" + clienteId, carrinho);

        salvarCarrinhoNosCookies(carrinho, response);

        response.sendRedirect("/listaCli");
    }


    private void salvarCarrinhoNosCookies(Map<Integer, Integer> carrinho, HttpServletResponse response) {
        StringBuilder carrinhoString = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : carrinho.entrySet()) {
            carrinhoString.append(entry.getKey()).append(":").append(entry.getValue()).append("_");
        }
        if (carrinhoString.length() > 0) {
            carrinhoString.deleteCharAt(carrinhoString.length() - 1);
        }
        Cookie carrinhoCookie = new Cookie("carrinho", carrinhoString.toString());
        carrinhoCookie.setMaxAge(48 * 60 * 60);
        response.addCookie(carrinhoCookie);
    }
    public Map<Integer, Integer> getCarrinhoFromCookiesPublic(HttpServletRequest request) {
        return getCarrinhoFromCookies(request);
    }

    private Map<Integer, Integer> getCarrinhoFromCookies(HttpServletRequest request) {
        Map<Integer, Integer> carrinho = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("carrinho".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    if (!cookieValue.isEmpty()) {
                        String[] itens = cookieValue.split("_");
                        for (String item : itens) {
                            String[] produtoInfo = item.split(":");
                            if (produtoInfo.length >= 2) {
                                try {
                                    int produtoId = Integer.parseInt(produtoInfo[0]);
                                    int quantidade = Integer.parseInt(produtoInfo[1]);
                                    carrinho.put(produtoId, quantidade);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return carrinho;
    }
}
