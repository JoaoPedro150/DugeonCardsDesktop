package com.tsi.app;

import java.io.IOException;

import com.tsi.card.Card;
import com.tsi.card.CardInteragivel;
import com.tsi.card.CardMutavel;
import com.tsi.card.Cards;
import com.tsi.card.Card.TipoCard;
import com.tsi.chars.Heroi;
import com.tsi.chars.Inimigo;
import com.tsi.chars.InimigoTransformavel;
import com.tsi.exception.InteracaoException;
import com.tsi.exception.MovimentoException;
import com.tsi.grid.Grid;
import com.tsi.grid.Posicao;
import com.tsi.itemespecial.Moeda;
import com.tsi.ui.Ajuda;
import com.tsi.ui.Sprite;

import javafx.geometry.Pos;

@SuppressWarnings("unused")
public class Jogo {
	private Grid grid;

	// Mantém a referência do objeto heroi.
	private Heroi heroi;

   // Mantém a referência dos objetos modificaveis.
	private CardMutavel cardsModificaveis[];

   // Mantém a referência dos objetos que estão sob efeito de alguma pocao.
   private Card cardsSobEfeitoPocao[];

   private Ajuda ajuda;

   private static final Posicao POSICAO_DE_INICIO = new Posicao(1, 1);

   private int qtdCardsRuim = 0;
   private int qtdCardsBom = 0;

   public Jogo() {
	   	grid = new Grid(POSICAO_DE_INICIO);
	   	Cards.carregarCards();

		Card card;
		
		heroi = new Heroi(25);
		heroi.setNome("Herói");
		heroi.setPosicao(POSICAO_DE_INICIO.clone());
		heroi.setSprite(new Sprite("Medusa.png"));
		ajuda = Ajuda.getInstance(DungeonCards.getStage());

		for (int i = 0; i < Grid.TAMANHO_X; i++)
			for (int j = 0; j < Grid.TAMANHO_Y; j++) {
				card = getRandomCard();
				card.setPosicao(new Posicao(i, j));
				grid.setCard(card);
			}
		

		grid.setCard(heroi);
	}
   
   public int getQtdMoedas() {
	   return heroi.getQtdMoedas();
   }

   private Card getRandomCard() {
	    Card card;

		if (qtdCardsBom - qtdCardsRuim  > 0) {
			card = Cards.getRandomCard(TipoCard.RUIM, heroi.getQtdMoedas());
			qtdCardsRuim++;
		}
		else if (qtdCardsRuim - qtdCardsBom  > 0) {
			card = Cards.getRandomCard(TipoCard.BOM, heroi.getQtdMoedas());
			qtdCardsBom++;
		}
		else {
			card = Cards.getRandomCard(heroi.getQtdMoedas());
		
			if (card.getTipoCard().equals(TipoCard.BOM))
				qtdCardsBom++;
			else
				qtdCardsRuim++;
		}

	   return card;
   }

   public boolean interagir() throws InteracaoException {
	   Boolean interacao = grid.getPosicaoCursor().isAdjacente(heroi.getPosicao());
	   boolean movimentoHeroi = false;

		if (interacao == Boolean.TRUE) {
			Card card = grid.getCard(grid.getPosicaoCursor());
			Card resultadoInteracao = null;

		   if (card != null) {
			   resultadoInteracao = ((CardInteragivel)card).interagir(heroi);

			   if (heroi.getArma() != null && heroi.getArma().getValor() <= 0)
				   heroi.setArma(null);
		   }
		   if (resultadoInteracao == null) {
			   card = getRandomCard();
			  
			   // Posicoes dos cantos
			   card = trocarPosicao(new Posicao(0, 0), 1, 0, 0, 1, card);
			   card = trocarPosicao(new Posicao(0, 2), 1, 2, 0, 1, card);
			   card = trocarPosicao(new Posicao(2, 2), 1, 2, 2, 1, card);
			   card = trocarPosicao(new Posicao(2, 0), 1, 0, 2, 1, card);
			   
			   // Posicao do centro
			   card = trocarPosicao(new Posicao(1, 1), 1, (heroi.getPosicao().getY() + ((heroi.getPosicao().getY() > grid.getPosicaoCursor().getY() ?  1 : -1))),
					   (heroi.getPosicao().getX() + ((heroi.getPosicao().getX() > grid.getPosicaoCursor().getX() ?  1 : -1))), 1, card);
			   
			   // Posicoes do meio
			   card = trocarPosicao(new Posicao(0, 1), 0, (heroi.getPosicao().getY() + ((heroi.getPosicao().getY() > grid.getPosicaoCursor().getY() ?  1 : -1))), 0, 2, card);
			   card = trocarPosicao(new Posicao(2, 1), 2, (heroi.getPosicao().getY() + ((heroi.getPosicao().getY() > grid.getPosicaoCursor().getY() ?  1 : -1))), 2, 0, card);
			   card = trocarPosicao(new Posicao(1, 0), 0, 0, (heroi.getPosicao().getX() + ((heroi.getPosicao().getX() > grid.getPosicaoCursor().getX() ?  1 : -1))), 0, card);
			   card = trocarPosicao(new Posicao(1, 2), 2, 2, (heroi.getPosicao().getX() + ((heroi.getPosicao().getX() > grid.getPosicaoCursor().getX() ?  1 : -1))), 2, card);
			   
			   card.setPosicao(heroi.getPosicao().clone());
			   grid.setCard(card);
			   
			   card = heroi;
			   movimentoHeroi = true;
		   }
		   else {
			   card = resultadoInteracao;
			   
			   if (card.getTipoCard().equals(TipoCard.BOM))
					qtdCardsBom++;
			   else
					qtdCardsRuim++;
		   }
		   
		   card.setPosicao(grid.getPosicaoCursor().clone());
		   grid.setCard(card);
		   return movimentoHeroi;
		}
		else if(interacao == null)
			throw new InteracaoException("Interação com o próprio herói.");

		throw new InteracaoException();
   }

   private Card trocarPosicao(Posicao posicao, int x, int y, int x1, int y2, Card card) {
	   Posicao posTroca;
	   Card cardAux = card;
	   
	   if (heroi.getPosicao().comparar(posicao)) {
		   if (grid.getPosicaoCursor().getX() == heroi.getPosicao().getX()) 
			   posTroca = new Posicao(x, y);
		   else
			   posTroca = new Posicao(x1, y2);
		   
		   cardAux = grid.getCard(posTroca);
		   card.setPosicao(posTroca);
		   grid.setCard(card);
		   return cardAux;
	   }
	   
	   return cardAux;
   }
   
   public Grid getGrid() {
	   return grid;
   }

	public void informacao() {
		Card card = grid.getCard(grid.getPosicaoCursor());
		ajuda.exibirAjuda(card.getInformacao(), card.getNome());

	}

	public void fecharInfo() {
		ajuda.esconderAjuda();

	}
}
