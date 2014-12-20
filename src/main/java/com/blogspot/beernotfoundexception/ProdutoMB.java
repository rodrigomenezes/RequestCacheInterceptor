package com.blogspot.beernotfoundexception;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.blogspot.beernotfoundexception.interceptors.RequestCache;

@Named
@SessionScoped
public class ProdutoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Produto> produtos;
	
	private static Logger log = Logger.getLogger(ProdutoMB.class.getSimpleName());

	@PostConstruct
	public void init() {
		produtos = new ArrayList<Produto>(15);
		for (int i = 1; i <= 15; i++) {
			Produto p = new Produto();
			p.setId((long) i);
			p.setNome("Produto " + i);
			Double valor = Math.random() * 50;
			p.setValor(valor);
			produtos.add(p);
		}
	}

	public List<Produto> getProdutos() {
		return produtos;
	}
	
	public void removerProduto(Produto p) {
		produtos.remove(p);
	}
	
	@RequestCache
	public boolean possuiPermissao(Produto p) {
		sleep();
		boolean resultado = p.getId() % 2 == 0 ? true : false;
		log.info(MessageFormat.format("possuiPermissao(Produto {0})",p.getId()));
		return resultado;
	}

	private void sleep() {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

