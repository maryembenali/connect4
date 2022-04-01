package connect;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Connect4  extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private static final int largeur, hauteur, largeurp, hauteurp, tablongueur, tabhauteur;
    private static JFrame frame;
    //private static JLabel label;
    private static Connect4 instance;
    private static Point p1, p2;
    private static int trials;
   // private static JButton button ;
    

    public static void main(String[] args) {
        instance = new Connect4();
    }

    public Connect4() {
        setBackground(Color.blue);
       //set the frame
        frame = new JFrame("connect4");
        frame.setBounds(50, 50, largeur, hauteur);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
       /* //set the label for the game / the welcome message
        label = new JLabel ("welcome to our game !");
        label.setBounds(40,40,100,100);
        label.add(label);
        label.setVisible(true);*/
        
        

        javax.swing.Timer timer = new javax.swing.Timer(2, this);
        timer.start();

        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
       // createbutton(frame);
    }
    
    public void createbutton (JFrame frame) {
        JButton button;
        TextArea textArea ;
        JPanel p = new JPanel();
        button = new JButton("Button");
        p.setLayout(null);
        textArea = new TextArea("click here");
        button.setBounds(0, 0, 100, 60);
        p.add(button);
        p.add(textArea,BorderLayout.NORTH);
        p.setVisible(true);
  }

    static {
        tablongueur = 7;
        tabhauteur = 6;
        largeurp =(int) 111.111111111111;
        largeur = (int)999.999999999999;
        hauteurp = 100;
        hauteur = 800;
    }

    public void actionPerformed(ActionEvent e) {
    	repaint();
    }

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        Board.draw(g);
    }

    public void mouseMoved(MouseEvent e) {
        Board.hover(e.getX()); //deplacer la piece a ajouter en en bougeant le curseur
    }

    public void mouseReleased(MouseEvent e) {
        Board.drop();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    static class PointPair { //creation de paire de point qui definissent la ligne des 4 cases successives de meme couleurs
        public Point p1, p2;

        PointPair(int x1, int y1, int x2, int y2) {
            p1 = new Point(x1, y1);
            p2 = new Point(x2, y2);
        }
    }

    static class Board {
        static Color[][] tab;
        static Color[] joueurs;
        static int tour;
        static int hoverX, hoverY;
        static boolean finjeu;
        static Color  blue = new Color(10, 28, 145);
       

        static {
            tab = new Color[tablongueur][tabhauteur];
            for (Color[] colors : tab) { //iteration dans un tableau de couleurs
                Arrays.fill(colors, Color.WHITE); //coloration initial du tableau en blanc
            }
            joueurs = new Color[]{Color.YELLOW, Color.RED}; //tableau contenant deux cases definissant les couleurs des joueurs
            tour = 0;
        }

        public static void draw(Graphics g) 
        //casting sur g pour profiter des fonctionnalités plus specifiques de la classe graphics2D
        {((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//for better graphics
        ((Graphics2D)(g)).setStroke(new BasicStroke(2.0f));
       
        //creation des  cercles qui apparaissent a l'ecran
            for (int i = largeurp; i <= largeur - largeurp ; i += largeurp) {
               if (i == largeur - largeurp) continue; 
                for (int j = hauteurp; j < hauteur - hauteurp; j += hauteurp) {  //
                	 g.setColor(tab[i/largeurp - 1][j/hauteurp - 1]);
                	  g.fillOval(i + 5, j + 5, largeurp - 10, hauteurp - 10);
                    g.setColor(Color.BLACK);
                    g.drawOval(i + 5, j + 5, largeurp - 10, hauteurp - 10);
                }
            }
            //cas du gain de l'un des 2 joueurs : on dessine une ligne reliant les pieces successives de meme couleurs et l'objet collé sr le curseur devient noir  
            g.setColor(finjeu ? Color.BLACK : joueurs[tour]);
            g.fillOval(hoverX + 5, hoverY + 5, largeurp - 10, hauteurp - 10);  //
            g.drawOval(hoverX + 5, hoverY + 5, largeurp - 10, hauteurp - 10);  //
            g.setColor(Color.GREEN); //couleur de la ligne
            if (p1 != null && p2 != null) {
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
           

        }
        public static void hover(int x) {
            x -= x%largeurp;
            if (x < largeurp) x = largeurp;
            if (x >= largeur - largeurp) x = largeur - 2*largeurp;
            hoverX = x;
            hoverY = 0;
        }

       

        public static void drop() { 
            if (tab[hoverX/largeurp - 1][0] != Color.WHITE) return;
            new Thread(() -> {
                Color color = joueurs[tour];
                int x = hoverX;
                int i;
                for (i = 0; i < tab[x/largeurp - 1].length && tab[x/largeurp - 1][i] == Color.WHITE; i++) {
                    tab[x/largeurp - 1][i] = color;
                    try { Thread.currentThread().sleep(200); } catch(Exception ignored) {}
                    tab[x/largeurp - 1][i] = Color.WHITE;
                    if (finjeu) return;
                }
                if (finjeu) return;
                tab[x/largeurp - 1][i - 1] = color;
                test(x/largeurp - 1, i - 1);
            }).start(); // thread 's parameter
            
            try { Thread.currentThread().sleep(100); } catch(Exception ignored) {}
            if (finjeu) return;
            tour = (tour + 1) % joueurs.length;
        }

        public static void test(int x, int y) {
            if (finjeu) return;

            PointPair pair = recherche(tab, x, y);

            if (pair != null) {
                p1 = new Point((pair.p1.x + 1) * largeurp + largeurp / 2, (pair.p1.y + 1) * hauteurp + hauteurp / 2);//affectation des cordonnées au point p1
                p2 = new Point((pair.p2.x + 1) * largeurp + largeurp / 2, (pair.p2.y + 1) * hauteurp + hauteurp / 2);//affectation des cordonnées au point p2
                frame.removeMouseListener(instance); //annuler les futures actions
                finjeu = true;
            } 
               	
        }
        
       
        public static PointPair recherche (Color[][] arr, int i, int j) {
            Color color = arr[i][j];
            int gauche, droite, haut, bas;

            // verifier horizontallement de gauche a droite
            gauche = droite = i;
            while (gauche >= 0 && arr[gauche][j] == color) gauche--;
            gauche++;
            while (droite < arr.length && arr[droite][j] == color) droite++;
            droite--;
            if (droite - gauche >= 3) {
                return new PointPair(gauche, j, droite, j);
            }

            // check vertically haut vers le bas
            bas = j;
            while (bas < arr[i].length && arr[i][bas] == color) bas++;
            bas--;
            if (bas - j >= 3) {
                return new PointPair(i, j, i, bas);
            }

            // verifier diagonalement a gauche du haut a droite du bas 
            gauche = droite = i;
            haut = bas = j;
            while (gauche >= 0 && haut >= 0 && arr[gauche][haut] == color) { gauche--; haut--; }
            gauche++; haut++;
            while (droite < arr.length && bas < arr[droite].length && arr[droite][bas] == color) { droite++; bas++; }
            droite--; bas--;
            if (droite - gauche >= 3 && bas - haut >= 3) {
                return new PointPair(gauche, haut, droite, bas);
            }

            // verifier diagonalement de la droite du haut a gauche du bas 
            gauche = droite = i;
            haut = bas = j;
            while (gauche >= 0 && bas < arr[gauche].length && arr[gauche][bas] == color) {gauche--; bas++;}
            gauche++; bas--;
            while (droite < arr.length && haut >= 0 && arr[droite][haut] == color) {droite++; haut--;}
           droite--; haut++;
            if (droite - gauche >= 3 && bas - haut >= 3) {
                return new PointPair(gauche, bas, droite, haut);
            }

            return null;
        }
        

        
    }
}
