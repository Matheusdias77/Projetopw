package com.example.demo.controller;

import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.DAO.ProdutoDAO;
import com.example.demo.model.Produto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoControllernew {
    @RequestMapping(value = "/addCarrinhoNew", method = RequestMethod.GET)
    public void addCarrinhoNew(@RequestParam String produtoId, @RequestParam String comando, HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);
        session.getAttribute("logado");

        ProdutoDAO produtoDAO = new ProdutoDAO();

        String command = request.getParameter("comando");
        if (command.equals("add")) {
            produtoDAO.removerDoEstoque(produtoId, 1);

        Cookie cookie = new Cookie("carrinho", produtoId);
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        response.sendRedirect("/listaCli");

        } else if (command.equals("remove")) {
            produtoDAO.adicionarAoEstoque(produtoId, 1);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie existingCookie : cookies) {
                    if (existingCookie.getName().equals("carrinho")) {
                        String carrinhoString = existingCookie.getValue();
                        carrinhoString = carrinhoString.replace(produtoId, "");
                        existingCookie.setValue(carrinhoString);
                        response.addCookie(existingCookie);

                    }
                }
            }

            response.sendRedirect("/listaCli");
        }

    }

    @RequestMapping(value = "/verCarrinhoNew", method = RequestMethod.GET)
    public void verCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        session.getAttribute("logado");

        var writer = response.getWriter();
        writer.println("<html>" + "<head></head>" + "<body>");
        writer.println("<a href='/listaCli'>Voltar</a>" + "<br>" + "<br>");
        writer.println("<table border='1'>");
        writer.println("<tr>");
        writer.println("<td> Nome </td>" + "<td> Descrição </td>" + "<td> preço </td>" + "<td> Remover </td>");
        writer.println("</tr>");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("carrinho")) {
                    String produtoId = cookie.getValue();
                    ProdutoDAO produtoDAO = new ProdutoDAO();
                    Produto produto = produtoDAO.buscarProdutoPorNome(produtoId);

                    if (produto != null) {

                        writer.println("<tr>");
                        writer.println("<td>" + produto.getNome() + "</td><td>" + produto.getDescricao() + "</td><td>" + produto.getPreco() + "</td><td><a href='/addCarrinhoNew?produtoId=" + produto.getNome() + "&comando=remove'>Remover</a></td>");
                        writer.println("</tr>");
                    }
                }
            }
        } else {

            writer.println("<tr>");
            writer.println("<h2>Carrinho Vasio" +  "</h2>");
            writer.println("</tr>");
        }

        writer.println("</table>");
        writer.println("<a href='/verCarrinho'>Finalizar compra</a>");
        writer.println("</body></html>");


    }

}
