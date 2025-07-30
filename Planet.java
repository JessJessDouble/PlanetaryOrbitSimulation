package PlanetaryOrbitSimulation;

import java.awt.*;
import java.util.ArrayList;
// import java.util.Random;

public class Planet {

    VectorP position, velocity;
    double diameter, mass;
    Color color, temp;
    double elasticity;
    // double spin;
    boolean highlight, remove;
    double constant = (6.6743 / (6.378 * 6.378) * 5.972);
    public ArrayList<VectorP> trail;
    public ArrayList<Planet> clones;
    public Planet clone;
    public int steps = 100000;
    static boolean updated;
    // private static final Random random = new Random();

    public Planet(VectorP position, VectorP velocity, Color color, double diameter,
            double elasticity, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.color = color;
        this.temp = color;
        this.diameter = diameter;
        this.elasticity = elasticity;
        // this.spin = random.nextDouble();
        this.mass = mass;
        this.remove = false;
        this.trail = new ArrayList<>();
        this.clone = this;
        updated = false;

    }

    public void show(Graphics2D g) {
        g.setColor(color);
        int size = (int) diameter;
        VectorP centerpos = position.subtract(new VectorP(size / 2, size / 2));
        g.fillOval((int) centerpos.x, (int) centerpos.y, size, size);
        highlight(g);

    }

    public void move() {
        VectorP velocityscale = velocity.scale(PlanetOrbitCanvas.scalenum);
        position = position.add(velocityscale);
    }

    public void collide(Planet p) {
        if (!remove && !p.remove) {

            if (PlanetOrbitCanvas.collisionOn) {
                // Collion detection on
                if (PlanetOrbitCanvas.bounce) {
                    // Bounce instead of combining
                    VectorP center1 = position;
                    VectorP center2 = p.position;
                    double distance = center1.distance(center2);
                    double minDistance = (diameter + p.diameter) / 2;

                    if (distance < minDistance) {
                        VectorP normal = center1.subtract(center2).normalize();
                        VectorP relativeVel = velocity.subtract(p.velocity);

                        // Apply impulse only if Planets are moving toward each other
                        if (relativeVel.dot(normal) < 0) {
                            double impulse = (2 * relativeVel.dot(normal)) / (mass + p.mass);
                            velocity = velocity.subtract(normal.scale(elasticity * impulse * p.mass));
                            p.velocity = p.velocity.add(normal.scale(elasticity * impulse * mass));
                        }

                        // Overlap correction: push Planets apart equally
                        double overlap = minDistance - distance;
                        VectorP correction = normal.scale(overlap * 0.5);
                        position = position.add(correction);
                        p.position = p.position.subtract(correction);

                        // Basic friction when resting vertically
                        if (Math.abs(velocity.y) < 0.1)
                            velocity.x *= 0.9;
                        if (Math.abs(p.velocity.y) < 0.1)
                            p.velocity.x *= 0.9;
                    }
                } else {
                    // Combine

                    double distance = position.distance(p.position);
                    double minDistance = (diameter + p.diameter) / 2;

                    if (distance < minDistance) {
                        velocity = new VectorP(
                                ((mass / (mass + p.mass)) * velocity.x + (p.mass / (mass + p.mass)) * p.velocity.x),
                                ((mass / (mass + p.mass)) * velocity.y + (p.mass / (mass + p.mass)) * p.velocity.y));
                        p.velocity = velocity;
                        position = new VectorP(
                                ((mass / (mass + p.mass)) * position.x + (p.mass / (mass + p.mass)) * p.position.x),
                                ((mass / (mass + p.mass)) * position.y + (p.mass / (mass + p.mass)) * p.position.y));
                        p.position = position.add(new VectorP(0.01, 0.01));
                        if (this.mass >= p.mass) {
                            mass += p.mass;
                            p.mass = mass;
                            this.remove = false;
                            p.remove = true;
                        } else {
                            mass += p.mass;
                            p.mass = mass;
                            this.remove = true;
                            p.remove = false;
                        }

                        diameter = 10 * PlanetOrbitCanvas.scalenum * Math.sqrt((mass * 4 / 3.14159));
                        p.diameter = diameter;

                    }
                }
            } else {
                // No detection
            }
        }
    }

    public void force(Planet p) {
        if (!remove && !p.remove) {
            double distance = position.distance(p.position) / (PlanetOrbitCanvas.scalenum);
            VectorP direction = p.position.subtract(position).normalize();
            // if (distance < diameter) {
            // Separate overlapping particles
            // double overlap = diameter - distance;
            // position = position.add(normal.scale(overlap / 2));
            // p.position = p.position.subtract(normal.scale(overlap / 2 + 1e-3));
            // }
            double distancesq = Math.max(distance * distance, 1); // Avoid division by zero
            double GForce = constant * (p.mass) / distancesq;
            double GForceP = constant * (mass) / distancesq;
            double maxForce = 1e3;
            GForce = Math.min(GForce, maxForce);
            GForceP = Math.min(GForceP, maxForce);
            VectorP force = direction.scale(GForce);
            VectorP forceP = direction.scale(GForceP);
            VectorP Accel = force.scale(1);
            VectorP AccelP = forceP.scale(-1);
            double maxAccel = 100; // Maximum allowable acceleration
            if (Accel.magnitude() > maxAccel) {
                Accel = Accel.normalize().scale(maxAccel);
            }
            if (AccelP.magnitude() > maxAccel) {
                AccelP = AccelP.normalize().scale(maxAccel);
            }
            velocity = velocity.add(Accel);
            p.velocity = p.velocity.add(AccelP);
        }
    }

    public void update(int width, int height, ArrayList<Planet> planets) {
        for (Planet p : planets) {
            if (p != this) {
                this.collide(p);
                this.force(p);
            }
        }
        if (highlight) {
            switch (PlanetOrbitCanvas.arrow) {
                case 1 -> this.position.y -= 1;
                case 2 -> this.position.y += 1;
                case 3 -> this.position.x -= 1;
                case 4 -> this.position.x += 1;
            }
        }
        if (!PlanetOrbitCanvas.space) {
            move();
        }
        // position.x = (position.x < 0) ? width - diameter : (position.x + diameter >
        // width) ? 0 : position.x;
        // position.y = (position.y < 0) ? height - diameter : (position.y + diameter >
        // height) ? 0 : position.y;
    }

    public void highlight(Graphics2D g) {
        if (highlight) {
            g.setColor(Color.BLACK);
            VectorP pos = position.subtract(new VectorP(diameter / 2, diameter / 2));
            g.drawOval((int) pos.x, (int) pos.y, (int) diameter, (int) diameter);
            color = Color.BLACK;
        } else {
            color = temp;
        }
    }

    public void selected(VectorP s1, VectorP s2) {
        if (PlanetOrbitCanvas.shift) {
            VectorP pCenter = position.add(new VectorP(diameter / 2, diameter / 2));
            VectorP sCenter = s1.add(s2.subtract(s1).scale(0.5));
            double dx = Math.abs(s1.x - s2.x) / 2 + diameter / 2;
            double dy = Math.abs(s1.y - s2.y) / 2 + diameter / 2;

            VectorP diff = pCenter.subtract(sCenter);
            if (Math.abs(diff.x) < dx && Math.abs(diff.y) < dy) {
                highlight = !highlight;
            }
        }

    }

    public void drawOrbit(Graphics2D g, Planet target, ArrayList<Planet> planets) {
        if (target.remove)
            return;

        // steps = 250000;

        if (updated && !clones.isEmpty()) {
            if (!clones.get(0).trail.isEmpty()) {
                VectorP TrailPoint = clones.get(0).trail.get(0);
                VectorP currentSimPosition = target.position;
                // System.err.println(TrailPoint.x);
                // System.err.println(currentSimPosition.x);
                for (int i = 1; i < 5; i++) {
                    // System.err.print(clones.get(0).trail.get(i).x+" ");

                }
                // System.out.println(" ");

                double tolerance = 1e-2;
                if (TrailPoint.distance(currentSimPosition) < tolerance) {
                    // System.out.println("END");
                    // Always redraw, even if trail size is only 1
                    appendOrbit();

                    redrawOrbit(g);
                    return;
                }
            }
        }

        // 1. Clone all planets
        clones = new ArrayList<>();
        for (Planet p : planets) {
            Planet c = new Planet(
                    p.position.copy(),
                    p.velocity.copy(),
                    p.temp,
                    p.diameter,
                    p.elasticity,
                    p.mass);
            c.trail = new ArrayList<>();
            clones.add(c);
        }

        // 2. Run simulation
        for (int step = 0; step < steps; step++) {
            for (Planet p : clones) {
                p.update(0, 0, clones);
            }
            for (Planet p : clones) {
                if (!p.remove) {
                    p.trail.add(p.position.copy());
                }
            }
        }
        System.err.println("oh");
        appendOrbit();
        redrawOrbit(g);
        updated = true;
    }

    public void redrawOrbit(Graphics2D g) {
        if (clones == null)
            return;
        g.setColor(new Color(0, 255, 255, 100));
        // for (Planet p : clones) {
        // for (int i = 1; i < p.trail.size(); i++) {
        // if (p.trail.get(i - 1).x>PlanetOrbitCanvas.width||p.trail.get(i -
        // 1).x<0||p.trail.get(i - 1).y>PlanetOrbitCanvas.height||p.trail.get(i -
        // 1).y<0) {
        // if(p.trail.get(i).x>PlanetOrbitCanvas.width||p.trail.get(i).x<0||p.trail.get(i).y>PlanetOrbitCanvas.height||p.trail.get(i).y<0){
        // continue;
        // }
        // }
        // VectorP p1 = p.trail.get(i - 1);
        // VectorP p2 = p.trail.get(i);
        // g.drawLine(
        // (int) p1.x,
        // (int) p1.y,
        // (int) p2.x,
        // (int) p2.y);
        // }
        // }
        for (Planet p : clones) {
            for (int i = 0; i < p.trail.size() - (int) Math.ceil(1 / (PlanetOrbitCanvas.scalenum)); i += Math
                    .ceil(1 / (PlanetOrbitCanvas.scalenum))) {
                // if(i==0){
                // System.err.println(i + Math.ceil(1/(PlanetOrbitCanvas.scalenum)));}

                if (p.trail.get(i + (int) Math.ceil(1 / (PlanetOrbitCanvas.scalenum))).x > PlanetOrbitCanvas.width
                        || p.trail.get(i + (int) Math.ceil(1 / (PlanetOrbitCanvas.scalenum))).x < 0
                        || p.trail
                                .get(i + (int) Math.ceil(1 / (PlanetOrbitCanvas.scalenum))).y > PlanetOrbitCanvas.height
                        || p.trail.get(i + (int) Math.ceil(1 / (PlanetOrbitCanvas.scalenum))).y < 0) {
                    if (p.trail.get(i).x > PlanetOrbitCanvas.width || p.trail.get(i).x < 0
                            || p.trail.get(i).y > PlanetOrbitCanvas.height || p.trail.get(i).y < 0) {
                        continue;
                    }
                }
                VectorP p1 = p.trail.get((int) (i));
                VectorP p2 = p.trail.get((int) (i + Math.ceil(1 / (PlanetOrbitCanvas.scalenum))));
                g.drawLine(
                        (int) p1.x,
                        (int) p1.y,
                        (int) p2.x,
                        (int) p2.y);
            }
        }
    }

    public void appendOrbit() {
        if (clones == null) {

            System.out.println("NOOOOO");
            return;

        }
        // System.out.println(clones.get(0).trail.size());
        for (Planet p : clones) {
            p.update(0, 0, clones);
            if (!p.remove) {
                p.trail.add(p.position.copy());
                // Instead of removing the first point blindly, limit size:
                if (p.trail.size() > steps + 1) {

                    p.trail.remove(0);

                }
            }
        }
    }

    public void moveOrbit(int x, int y) {
        if (clones == null) {

            return;

        }
        for (Planet p : clones) {
            for (VectorP v : p.trail) {
                v.x += x;
                v.y += y;
            }
            p.position.x += x;
            p.position.y += y;
        }
    }

}
