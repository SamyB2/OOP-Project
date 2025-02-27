package Src;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import Src.Menus.Menu;
import Src.ModeleUtils.Joueur;
import Src.ViewUtils.ImagePanel;

import java.io.IOException;

public class GameView extends JFrame {
    
    private Controller controller ; 
    private PlateauView plateau ; 
    private JoueurView [] joueurs ; // joueurs[i] correspond a l'interface pour joueur[i] dans GameModel 
    private int [] posSelected ;
    private ImagePanel a;
    private char jeu;
    private JButton mute;
    private boolean sonON = false;
 
    public GameView (int joueursH , String [] pseudos , char jeu) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        super();
        Menu.clip.close();
        Menu.game = AudioSystem.getAudioInputStream(Menu.file2);;
        Menu.clip.open(Menu.game);

        setBackground(new Color(0, 51, 102));
        setTitle("Domino & Carcasonne");
        setSize(1200, 800);
        getContentPane().setLayout(null);
        setResizable(false);
        setBackground(new Color(0, 51, 102));

        this.jeu = jeu;
        // Creation et placement du plateau
        plateau = new PlateauView() ;
        add(plateau);        
        plateau.horizontal.setBounds(1050, 50, 110, 35);
        add(plateau.horizontal) ; 

        // Creation et placement des interfaces de jeu des joueurs humain
        joueurs = new JoueurView[joueursH] ; 
        for (int i = 0 ; i<joueursH ; i++) {
            joueurs[i] = new JoueurView(i ,pseudos[i]);
            add(joueurs[i]) ; 
        }
        
        posSelected = new int [2] ; 
        posSelected[0] = -1 ;
        posSelected[1] = -1 ; 

        ImageIcon m = new ImageIcon("Pictures/muteON.png");
        mute = new JButton(m);
        add(mute);
        mute.setBounds(1000, 50, 35, 35);

        //Background du gameview
        a = new ImagePanel("Pictures/GameViewBack.png");
        a.setSize(new Dimension(1200, 800));
        add(a);
        setActionMute();
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mute.doClick();
        mute.doClick();
    }
    
    public void setActionMute(){
        mute.addActionListener(ev ->{
            ImageIcon icon2;
            if (sonON == true) {
                icon2 = new ImageIcon("Pictures/muteON.png");
                //clip.setFramePosition(0);
                Menu.clip.start();
                sonON = false;
 
            } else {
                icon2 = new ImageIcon("Pictures/muteOFF.png");
                //clip.setFramePosition(0);
                Menu.clip.stop();
                sonON = true;
            }
 
            mute.setIcon(icon2);
        });
    }

    public void finishGame(Joueur joueur, boolean bot){
        a.setVisible(false);
        remove(a);
        plateau.setVisible(false);
        remove(plateau);
        for(JoueurView j : joueurs){
            j.setVisible(false);
            this.remove(j);
        }
        JLabel name;
        if(bot){
            name = new JLabel("BOT");
        }else{
           name = new JLabel(joueur.getPseudo());
        }
        name.setBounds(480, 222, 299, 69);
        name.setFont(new Font("HG-GothicB-Sun", Font.PLAIN, 80));
        name.setForeground(Color.white);
        add(name);
        name.setVisible(true);

        JLabel score = new JLabel(String.valueOf(joueur.getNbPoints()));
        score.setBounds(496, 390, 299, 69);
        score.setFont(new Font("HG-GothicB-Sun", Font.PLAIN, 80));
        score.setForeground(Color.white);
        add(score);
        score.setVisible(true);

        JButton quitter = new JButton("Quitter");
        quitter.setFont(new Font("HG-GothicB-Sun", Font.PLAIN, 80));
        quitter.setBounds(418, 612, 320, 99);
        add(quitter);
        quitter.setVisible(true);
        quitter.addActionListener(ev ->{
            this.dispose();
            Menu.clip.close();
            try {
                @SuppressWarnings("unused") 
                Menu a = new Menu();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        });
        ImagePanel end = new ImagePanel("Pictures/GameViewBackEnd.png");
        end.setSize(new Dimension(1200, 800));
        add(end);
        end.setVisible(true);
    }

    public void firstStep (JLabel tuile , int [] lim){ 
        plateau.addTuile(40, 40, tuile);
        plateau.update(lim);
    }

    public void setActionButtons () {
        for (JoueurView x : joueurs) {
            x.setActionButtons();
        }
    }

    public void updatePlateauView (JLabel jLabel , int [] limite) {
        plateau.addTuile(posSelected[0], posSelected[1], jLabel);
    }

    public void updatePlateauView (int x , int y , JLabel jLabel ,int [] limite) {
        plateau.addTuile(x, y, jLabel);
    }
    
    public void updateJoueurView (int score , JLabel jLabel ,int id) {
        joueurs[id].update(score, jLabel);
    }
    
    public void showJoueurView (int id) {
        joueurs[((id-1)%joueurs.length+joueurs.length)%joueurs.length].setVisible(false); ;
        joueurs[id].setVisible(true);
    }

    public void setController (Controller controller) {
        this.controller = controller ; 
    }

    private class JoueurView extends JPanel {
        private JButton poser = new JButton("POSER") ; 
        private JButton skip = new JButton("SKIP"); 
        private JButton abandonner = new JButton("ABANDONNER") ;
        private JButton tournerDroite = new JButton("TOURNERD") ; 
        private JButton tournerGauche = new JButton("TOURNERG") ; 
        private JButton pion = new JButton("PION"); 
        private int id ;       
        
        private JLabel score ;
        private JLabel pseudo ;
        private JLabel tuile; 
        private int sc = 0;
        
        JoueurView (int id , String pseudo ) {
            setBackground(new Color(59, 46, 92));
            this.id = id ;  
            score = new JLabel("SCORE :" + sc ) ; 
            this.pseudo = new JLabel("PSEUDO : "+ pseudo) ;
            
            //Sizing
            setSize(new Dimension(348, 528));
            setLayout(null);
            setLocation(812,143);
            poser.setSize(new Dimension(254, 61));
            skip.setSize(new Dimension(120, 37));
            abandonner.setSize(new Dimension(120, 37));
            tournerDroite.setSize(new Dimension(120, 37));
            tournerGauche.setSize(new Dimension(120, 37));
            score.setSize(new Dimension(254, 61));
            this.pseudo.setSize(new Dimension(254, 61));
            pion.setSize(new Dimension(120, 37));
            
            //Styling de score et pseudo
            score.setForeground(Color.WHITE);
            score.setFont(new Font("HG-GothicB-Sun", Font.PLAIN, 20));
            this.pseudo.setForeground(Color.WHITE);
            this.pseudo.setFont(new Font("HG-GothicB-Sun", Font.PLAIN, 20));

            //Location
            this.pseudo.setLocation(28, 25);
            score.setLocation(213, 25);
            poser.setLocation(49, 302);
            tournerDroite.setLocation(184, 424);
            tournerGauche.setLocation(49,424);
            skip.setLocation(49, 378);
            abandonner.setLocation(184, 378);
            pion.setLocation(95, 470);
            
            add(score);
            add(this.pseudo);
            add(abandonner);
            add(poser);
            add(skip);
            add(tournerGauche);
            add(tournerDroite);
            if (jeu == 'C') {
                add(pion) ; 
                pion.setVisible(true);
            }
           
            //SetVisible des boutons et de l'affichage
            poser.setVisible(true);
            skip.setVisible(true);
            tournerDroite.setVisible(true);
            tournerGauche.setVisible(true);
            abandonner.setVisible(true);
            this.setVisible(false);
        }

        public void setActionButtons () {
            poser.addActionListener(ev -> {
                if (posSelected[0] != -1) {
                    controller.poser(posSelected[0] , posSelected[1]) ; 
                }
            });
        
            skip.addActionListener(ev -> {
                controller.next() ; 
            });
        
            abandonner.addActionListener(ev -> {
                controller.abandonne() ; 
                controller.next() ; 
            });
        
            tournerGauche.addActionListener(ev -> {
                controller.tourner(false) ; 
            });
    
            tournerDroite.addActionListener(ev -> {
                controller.tourner(true) ; 
            });

            pion.addActionListener(ev -> {
                if (posSelected[0] != -1) {
                    controller.pion(posSelected[0] , posSelected[1] , id) ; 
                }
            });
        }
    
        public void setTuile (JLabel jLabel) {
            this.tuile = jLabel ; 
        }

        public void update (int score , JLabel jLabel) {            
            if(this.tuile != null){
                tuile.setVisible(false);
                this.remove(tuile);
            }
            setTuile(jLabel);
            add(tuile);
            tuile.setLocation(50, 80);
            tuile.setVisible(true);
            this.sc  = score;
            this.score.setText("SCORE : " + sc);
            this.score.setVisible(true);
        }
   
    }

    private class PlateauView extends JPanel implements MouseWheelListener {
        private Border blackline = BorderFactory.createLineBorder(Color.WHITE,2); 
        private JCheckBox horizontal ; 
        private Case[][] plateauView ; 
        private int [] limites ; 

        PlateauView () { 
            setSize(new Dimension(776, 615));
            setLocation(13,109);
            addMouseWheelListener(this);

            plateauView = new Case[80][80] ; 
            for (int i = 0 ; i<plateauView.length ; i++) {
                for (int k = 0 ; k<plateauView[i].length ; k++) {
                    plateauView[i][k] = new Case(k, i); 
                    plateauView[i][k].setBorder(blackline);
                }
            }
            limites = new int [4];
            limites[0] = 39 ; 
            limites[1] = 39 ; 
            limites[2] = 41 ; 
            limites[3] = 41 ;  
            horizontal = new JCheckBox("Horizontale") ; 
            horizontal.setSelected(false);

            setVisible(true);
            setOpaque(false);
        }

        public void addTuile (int x , int y , JLabel jLabel) {
            plateauView[y][x].removeAll();
            plateauView[y][x].add(jLabel);
        }

        public void update (int [] lim) {
            this.removeAll();
            this.setLayout(new GridLayout(lim[3]-lim[1]+1, lim[2]-lim[0]+1 ));
            for(int i= lim[1]; i<=lim[3]; i++){
                for(int j=lim[0]; j<=lim[2]; j++){
                    plateauView[i][j].setBorder(blackline);
                    this.add(plateauView[i][j]);
                }
            }
        }

        public void unselect (int x , int y) {
            // cette fonction va unselect 
            // la case a la pos (x;y)
            ((GameView.PlateauView.Case) plateauView[y][x]).selected(false);
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int [] limites = controller.getLimite() ; 
            if (e.getWheelRotation()>0) {
                if (!horizontal.isSelected()) {
                    if (this.limites[1] > limites[1]) {
                        this.limites[1]-=1 ; 
                        this.limites[3]-=1 ; 
                    }
                }else{
                    if (this.limites[0] > limites[0]) {
                        this.limites[0]-=1 ;
                        this.limites[2]-=1 ; 
                    }
                }
            }else{
                if (!horizontal.isSelected()) {
                    if (this.limites[3] < limites[3]) {
                        this.limites[3]+=1 ; 
                        this.limites[1]+=1 ; 
                    }
                }else{
                    if (this.limites[2] < limites[2]) {
                        this.limites[2]+=1 ;
                        this.limites[0]+=1 ; 
                    }
                }
            }
            update(this.limites);
            this.validate();
        }

        private class Case extends JPanel implements MouseInputListener {
            private int x ; 
            private int y ; 

            Case (int x , int y) {
                this.x = x ; 
                this.y = y ; 
                addMouseListener(this);
                addMouseMotionListener(this);
                setVisible(true);
                setBackground(Color.BLACK);
            }

            public void selected (boolean green) {
                if (green) {
                    setBackground(Color.GREEN);
                }else{
                    setBackground(Color.BLACK);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (posSelected[0] == x && posSelected[1] == y) {
                    selected(false);
                    posSelected[0] = -1 ; 
                    posSelected[1] = -1 ; 
                    return ; 
                }
            
                if (posSelected[0] != -1) {
                    PlateauView.this.unselect(posSelected[0] , posSelected[1]) ;
                }
                selected(true);
                posSelected[0] = x ; 
                posSelected[1] = y ; 
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                
            }

        }

    }
}