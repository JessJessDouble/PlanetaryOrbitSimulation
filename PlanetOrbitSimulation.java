package PlanetaryOrbitSimulation;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;

class PlanetOrbitCanvas extends JPanel {

    static ArrayList<Planet> planets = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    static boolean collisionOn, bounce, shift, delete, escape, space, solarsystem;
    static int arrow;
    static double scalenum = 1;
    static double speedup = 1;
    static double width;
    static double height;
    Color J =new Color(165,145,134);
    Color U =new Color(213, 251, 252);
    Color S = new Color(234,214,184);
    Color V = new Color(227,158,28);
    int currentplanetcount;
    boolean highlightCount;
    Color colorRand;
    static Graphics2D g2d;
    int diameter = 2;
    double elasticity = .81;
    Random random = new Random();
    VectorP select1 = new VectorP(0, 0);
    VectorP select2 = new VectorP(0, 0);
    VectorP position = new VectorP(0, 0);
    VectorP positionm = new VectorP(0, 0);
    VectorP positions = new VectorP(0, 0);

    public PlanetOrbitCanvas() {
            solarsystem = true;

        if(solarsystem){
        Planet Sun = new Planet(new VectorP(250, 250), new VectorP(0, 0),
        Color.YELLOW,
        // 50 * Math.sqrt(333054.253182 * 4 / 3.14159)
        250* 109.75227344
        , elasticity, 333054.253182);
        Planet Mercury = new Planet(new VectorP(45468.7989966 + 250, 250), new
        VectorP(0, 3.71589840075), Color.GRAY,
        250 * 
        0.38*5
        // Math.sqrt(0.0550066979236 * 4 / 3.14159)
        , elasticity,
        0.0550066979236);
        Planet Venus = new Planet(new VectorP(84829.8291 + 250, 250), new VectorP(0,
        2.74537472562), V, 
        250 * 
        0.95*5
        // Math.sqrt(0.814969859344 * 4 / 3.14159)
        , elasticity, 0.814969859344);
        Planet Earth = new Planet(new VectorP(116807.776733 + 250, 250), new
        VectorP(0, 2.33066792098), Color.BLUE,
        250 * 
        // 5
        Math.sqrt(1 * 4 / 3.14159)
        , elasticity, 1);
        Planet Moon = new Planet(new VectorP(116807.776733 + 250 + 301.348385074, 250), new
        VectorP(0, 2.33066792098+0.0801975540922), Color.GRAY,
        25 * Math.sqrt(0.0123035383289 * 4 / 3.14159), elasticity, 0.0123035383289);
        Planet Mars = new Planet(new VectorP(178661.022264 + 250, 250), new
        VectorP(0, 1.88750391972), Color.RED,
        250 * 
        .53*5
        // Math.sqrt(0.106999330208 * 4 / 3.14159)
        , elasticity, 0.106999330208);
        Planet Jupiter = new Planet(new VectorP(603488.554406 + 250, 250), new
        VectorP(0, 1.02696770147), J,
        250 * 
        11*5
        // Math.sqrt(317.838245144 * 4 / 3.14159)
        , elasticity, 317.838245144);
        Planet Saturn = new Planet(new VectorP(1122138.60144 + 250, 250), new
        VectorP(0, 0.759642521167), S,
        250 *
        9*5
        // Math.sqrt(95.1607501674 * 4 / 3.14159)
        , elasticity, 95.1607501674);
        Planet Uranus = new Planet(new VectorP(2288413.2957 + 250, 250), new
        VectorP(0, 0.533866415804), U,
        250 * 
        4*5
        // Math.sqrt(14.5361687877 * 4 / 3.14159)
        , elasticity, 14.5361687877);
        Planet Neptune = new Planet(new VectorP(3504860.45782 + 250, 250), new
        VectorP(0, 0.425682031985), Color.BLUE,
        250 * 3.9
        // Math.sqrt(17.1466845278 * 4 / 3.14159)
        , elasticity, 17.1466845278);
        planets.add(Sun);
        planets.add(Mercury);
        planets.add(Venus);
        planets.add(Earth);
        planets.add(Moon);
        planets.add(Mars);
        planets.add(Jupiter);
        planets.add(Saturn);
        planets.add(Uranus);
        planets.add(Neptune);}
        collisionOn = true;
        bounce = false;
        Timer timer = new Timer(10, e -> {
            // for (Particle p : particles) {
            // p.update(getWidth(), getHeight(), particles);

            // }
            planets.removeIf(planet -> planet.remove);
            repaint();
        });
        timer.start();

        // for (int i = 0; i < 10; i++) {
        // double zoomFactor = 0.5;
        // VectorP center = new VectorP(250, 250);
        // for (Planet p : PlanetOrbitCanvas.planets) {
        // p.position = center.add((p.position.subtract(center)).scale(zoomFactor));
        // p.diameter *= zoomFactor;

        // }
        // PlanetOrbitCanvas.scalenum *= zoomFactor;
        // System.out.println(PlanetOrbitCanvas.scalenum);
        // }

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                positionm = new VectorP(e.getX(), e.getY());
                positions = new VectorP(e.getX(), e.getY());
                select1 = new VectorP(e.getX(), e.getY());
                colorRand = Color.BLACK;

            }

            @Override
            public void mouseDragged(MouseEvent e) {

                positions = new VectorP(e.getX(), e.getY());

            }

        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                position = new VectorP(e.getX(), e.getY());
                if (shift) {
                    select1 = new VectorP(e.getX(), e.getY());

                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if (!shift) {
                    position = new VectorP(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                select2 = new VectorP(e.getX(), e.getY());
                VectorP velocity = select2.subtract(position);
                velocity = new VectorP(velocity.x * 0.1 / scalenum, velocity.y * 0.1 / scalenum);
                if (!shift) {
                    for (Planet p : planets) {
                        p.highlight = false;
                    }
                    colorRand = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                    // int mass = scanner.nextInt();
                    int mass = 1;
                    planets.add(
                            new Planet(position.add(new VectorP(random.nextInt(2), random.nextInt(1))), velocity,
                                    colorRand, 10 * scalenum * Math.sqrt(4 * mass / 3.14159), elasticity, mass));

                }
                for (Planet p : planets) {
                    p.selected(select1, select2);
                }

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        width = getWidth();
        height = getHeight();
        // for (int i = 0; i < speedup; i++) {

        for (Planet p : planets) {
            p.show(g2d);
            p.update(getWidth(), getHeight(), planets);
            if (!planets.isEmpty() && planets.indexOf(p) == 0) {
                planets.get(0).drawOrbit(g2d, planets.get(0), planets);
            }
        }
        // }

        currentplanetcount = planets.size();

        if (PlanetOrbitCanvas.escape) {
            for (Planet p : planets) {
                p.highlight = false;
            }
        }
        if (delete) {
            for (int index = 0; index < planets.size(); index++) {
                if (planets.get(index).highlight) {
                    planets.remove(planets.get(index));
                    index--;
                }
            }
            delete = false;
        }
        if (shift) {
            g2d.setColor(Color.BLACK);
            int x = (int) Math.min(select1.x, positions.x);
            int y = (int) Math.min(select1.y, positions.y);
            int width = (int) Math.abs(select1.x - positions.x);
            int height = (int) Math.abs(select1.y - positions.y);
            g2d.drawRect(x, y, width, height);

        }
        for (int index = 0; index < planets.size(); index++) {
            highlightCount = false;

            if (planets.get(index).highlight) {
                highlightCount = true;
                break;
            }
        }
        if (!highlightCount) {
            for (Planet p : planets) {
                if (arrow == 1) {
                        p.position.y += 5;
                        p.moveOrbit(0, 5);
                    }
                    if (arrow == 2) {
                        p.position.y -= 5;
                        p.moveOrbit(0, -5);
                    }

                    if (arrow == 3) {
                        p.position.x += 5;
                        p.moveOrbit(5, 0);
                    }

                    if (arrow == 4) {
                        p.position.x -= 5;
                        p.moveOrbit(-5, 0);
                    }

                

            }


        }
    }
}

class PlanetOrbitSimulationKeySet extends JPanel implements KeyListener {
    public PlanetOrbitSimulationKeySet() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        System.out.println("Key pressed: " + e.getKeyChar());
        if (code == KeyEvent.VK_UP) {
            PlanetOrbitCanvas.arrow = 1;
            System.out.println("UP");

        }
        if (code == KeyEvent.VK_DOWN) {
            PlanetOrbitCanvas.arrow = 2;
            System.out.println("DOWN");

        }
        if (code == KeyEvent.VK_LEFT) {
            PlanetOrbitCanvas.arrow = 3;
            System.out.println("LEFT");

        }
        if (code == KeyEvent.VK_RIGHT) {
            PlanetOrbitCanvas.arrow = 4;
            System.out.println("RIGHT");

        }
        if (code == KeyEvent.VK_SHIFT) {
            PlanetOrbitCanvas.shift = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            PlanetOrbitCanvas.escape = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            System.out.println("Pause");

            System.out.println(PlanetOrbitCanvas.space);
            PlanetOrbitCanvas.space = !PlanetOrbitCanvas.space;
        }
        if (e.isControlDown() && code == KeyEvent.VK_A) {
            for (Planet p : PlanetOrbitCanvas.planets) {
                p.highlight = true;
            }
        }
        if (code == KeyEvent.VK_EQUALS) {
            double zoomFactor = 2.0;
            VectorP center = new VectorP(PlanetOrbitCanvas.width / 2, PlanetOrbitCanvas.height / 2);
            for (Planet p : PlanetOrbitCanvas.planets) {
                p.position = center.add(p.position.subtract(center).scale(zoomFactor));
                p.diameter *= zoomFactor;
                // p.velocity = p.velocity.scale(Math.sqrt(zoomFactor)); // scale velocity by
                // √zoom
            }
            PlanetOrbitCanvas.scalenum *= zoomFactor;
        }
        if (code == KeyEvent.VK_MINUS) {
            double zoomFactor = 0.5;
            VectorP center = new VectorP(PlanetOrbitCanvas.width / 2, PlanetOrbitCanvas.height / 2);
            for (Planet p : PlanetOrbitCanvas.planets) {
                p.position = center.add((p.position.subtract(center)).scale(zoomFactor));
                p.diameter *= zoomFactor;

            }
            PlanetOrbitCanvas.scalenum *= zoomFactor;
            System.out.println(PlanetOrbitCanvas.scalenum);
        }
        if (code == KeyEvent.VK_PERIOD) {
            PlanetOrbitCanvas.speedup *= 2;
        }
        if (code == KeyEvent.VK_COMMA) {
            PlanetOrbitCanvas.speedup /= 2;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP) {
            PlanetOrbitCanvas.arrow = 0;
            System.out.println("UP");
        }
        if (code == KeyEvent.VK_DOWN) {
            PlanetOrbitCanvas.arrow = 0;
            System.out.println("DOWN");
        }
        if (code == KeyEvent.VK_LEFT) {
            PlanetOrbitCanvas.arrow = 0;
            System.out.println("LEFT");
        }
        if (code == KeyEvent.VK_RIGHT) {
            PlanetOrbitCanvas.arrow = 0;
            System.out.println("RIGHT");
        }
        if (code == KeyEvent.VK_SHIFT) {
            System.out.println("Shift");
            PlanetOrbitCanvas.shift = false;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            System.out.println("Esc");
            PlanetOrbitCanvas.escape = false;
        }
    }
}

class maps {
    public double map(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return (toHigh - toLow) * (value - fromLow) / (fromHigh - fromLow) + toLow;
    }
}

public class PlanetOrbitSimulation {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Planetary Orbit Simulation");
        PlanetOrbitCanvas canvas = new PlanetOrbitCanvas();
        PlanetOrbitSimulationKeySet panel = new PlanetOrbitSimulationKeySet();
        frame.add(canvas);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(panel);
        frame.setVisible(true);
    }
}