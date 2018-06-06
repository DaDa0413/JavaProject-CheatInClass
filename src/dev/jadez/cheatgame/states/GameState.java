package dev.jadez.cheatgame.states;

import java.awt.Graphics;

import dev.jadez.cheatgame.Game;
import dev.jadez.cheatgame.entities.ProgressBar;
import dev.jadez.cheatgame.entities.creatures.Enemy;
import dev.jadez.cheatgame.entities.creatures.Player;
import dev.jadez.cheatgame.entities.creatures.Teacher;
import dev.jadez.cheatgame.entities.fixedObject.Desk;

public class GameState extends State{

	private Player player;
	private Teacher teacher;
	private Enemy enemy;
	private Desk[] deskArray;
	private Desk forSetDesk;
	private ProgressBar progressbar;
	private boolean full;//progress bar full
	
	public GameState(Game game) {
		super(game);
		player = new Player(game,100,100,32, 32);
		teacher = new Teacher(game, 150, 150, 32, 32, 500, 20);//x, y, width, height
																// scanHeight, theta
		progressbar = new ProgressBar(game);
		enemy = new Enemy(500, 500, 32, 32);
		//a little bit tricky here, I use a method in class Desk to initialize all the table posiotion
		//coz I don't want GameState to be lengthy
		deskArray = new Desk[17];
		for(int i = 0; i < 17; i++)
			deskArray[i] = new Desk(0, 0, 0, 0);	//temporary position we have to correct
		forSetDesk = new Desk(0, 0, 0, 0);
		forSetDesk.setDeskPosition(deskArray);
		
	}
	
	public void tick() {
		player.tick();
		teacher.tick();
		enemy.tick();

		
		//Collision detection
		if(teacher.getpoly().contains(player.getPlayerPosition()))
			System.out.println("got you, bitch");
		for(int i = 0; i < 17; i++)
		{
			if(deskArray[i].getRange().contains(player.getPlayerPosition()))
				System.out.println("bang");
		}
		if(enemy.getRange().contains(player.getPlayerPosition()))
		{
			//player enter the cheat zone
			progressbar.tick(true);
		}
		else
			progressbar.tick(false);
		
		full = progressbar.getFull();
		if(full)
			System.out.println("win");
	}


	public void render(Graphics g) {
		player.render(g);
		teacher.render(g);
		for(int i = 0; i < 17; i++)
			deskArray[i].render(g);
		progressbar.render(g);
		enemy.render(g);
	}
	
	

}
