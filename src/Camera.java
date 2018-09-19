/*
  simulate "natural viewing"
  from E, A, B, C
*/

import java.util.Scanner;

public class Camera {

    private static Triple up = new Triple(0, 0, 1);

    // human modeling of viewing setup
    private Triple e;
    private double azi, alt, d;  // d = distance from E to A
    private double size;    // size = distance from A to B, and A to C

    // crucial derived quantities
    private Triple a, b, c;

    // derived data (from e, a, b, c)
    private Triple eMinusA, bMinusA, cMinusA;
    private double len2EA, len2BA, len2CA;

    public Camera(Triple eIn, double aziIn, double altIn, double dIn, double sizeIn) {
        e = eIn;
        azi = aziIn;
        alt = altIn;
        d = dIn;
        size = sizeIn;

        update();
    }

    // compute the coordinates of p in the view screen
    public Triple render(Triple p) {

        Triple pMinusE = p.minus(e);

        double lambda = -len2EA / eMinusA.dot(pMinusE);

        double beta = lambda * bMinusA.dot(pMinusE) / len2BA;
        double gamma = lambda * cMinusA.dot(pMinusE) / len2CA;

        if (lambda < 0)
            lambda = -2;

        return new Triple(beta, gamma, lambda);

    }

    // from e, azi, alt, d, and size,
    //  compute e, a, b, c
    public void update() {

        // compute a:
        double alpha = Math.toRadians(azi);  // azi, alt are in degrees
        double beta = Math.toRadians(alt);
        a = new Triple(e.x + Math.cos(beta) * d * Math.cos(alpha),
                e.y + Math.cos(beta) * d * Math.sin(alpha),
                e.z + d * Math.sin(beta)
        );

        Triple aTowardsE = a.vectorTo(e).normalize();

        Triple aTowardsB = up.cross(aTowardsE).normalize();
        Triple aTowardsC = aTowardsE.cross(aTowardsB);

        Triple b = a.add(aTowardsB.mult(size));
        Triple c = a.add(aTowardsC.mult(size));

        // compute other derived data for efficiency:

        eMinusA = e.minus(a);
        bMinusA = b.minus(a);
        cMinusA = c.minus(a);

        double tol = 1e-7;

        if (Math.abs(eMinusA.dot(bMinusA)) > tol ||

                Math.abs(eMinusA.dot(cMinusA)) > tol ||
                Math.abs(bMinusA.dot(cMinusA)) > tol
        ) {
            System.out.println("Invalid arguments to constructor---not perpendicular");
            System.exit(1);
        }

        len2EA = eMinusA.dot(eMinusA);
        len2BA = bMinusA.dot(bMinusA);
        len2CA = cMinusA.dot(cMinusA);

    }

    public void shift(double dx, double dy, double dz) {
        e = e.add(new Triple(dx, dy, dz));
        update();
    }

    public void rotate(double amount) {
        azi += amount;

        // adjust to stay in [0,360) range
        if (azi < 0) {
            azi += 360;
        }
        if (azi >= 360) {
            azi -= 360;
        }

        update();
    }

    public String toString() {
        return String.format("Eye: %.2f %.2f %.2f Azi: %d Alt: %d",
                e.x, e.y, e.z, (int) azi, (int) alt);
    }
/*
  public static void main( String[] args ) {
     Camera cam = new Camera( new Triple(50,50,0),
                              225, 0, 2, 1 );
     Scanner keys = new Scanner( System.in );
    System.out.print("enter any point P in space: ");
     Triple p = new Triple( keys );

     Triple results = cam.render( p );

  }
*/

    // this main does stand-alone testing of entering e, a, b, c and
    //  and p and computing things like in Ex4
    public static void main(String[] args) {

        Scanner keys = new Scanner(System.in);

        System.out.print("enter E: ");
        Triple e = new Triple(keys);
        System.out.print("enter A: ");
        Triple a = new Triple(keys);
        System.out.print("enter B: ");
        Triple b = new Triple(keys);
        System.out.print("enter C: ");
        Triple c = new Triple(keys);

        System.out.print("enter any point P1 in space: ");
        Triple p1 = new Triple(keys);

        System.out.print("enter any point P2 in space: ");
        Triple p2 = new Triple(keys);

        System.out.print("enter any point P3 in space: ");
        Triple p3 = new Triple(keys);


        Triple eMinusA = e.minus(a);
        Triple bMinusA = b.minus(a);
        Triple cMinusA = c.minus(a);
        Triple p1MinusE = p1.minus(e);
        Triple p2MinusE = p2.minus(e);
        Triple p3MinusE = p3.minus(e);

        double lambda1 = -eMinusA.dot(eMinusA) / eMinusA.dot(p1MinusE);
        double beta1 = lambda1 * bMinusA.dot(p1MinusE) / bMinusA.dot(bMinusA);
        double gamma1 = lambda1 * cMinusA.dot(p1MinusE) / cMinusA.dot(cMinusA);

        double lambda2 = -eMinusA.dot(eMinusA) / eMinusA.dot(p2MinusE);
        double beta2 = lambda2 * bMinusA.dot(p2MinusE) / bMinusA.dot(bMinusA);
        double gamma2 = lambda2 * cMinusA.dot(p2MinusE) / cMinusA.dot(cMinusA);

        double lambda3 = -eMinusA.dot(eMinusA) / eMinusA.dot(p3MinusE);
        double beta3 = lambda3 * bMinusA.dot(p3MinusE) / bMinusA.dot(bMinusA);
        double gamma3 = lambda3 * cMinusA.dot(p3MinusE) / cMinusA.dot(cMinusA);

        System.out.println("beta1 = " + beta1 + " gamma1 = " + gamma1 +
                " lambda1 = " + lambda1);

        System.out.println("beta2 = " + beta2 + " gamma2 = " + gamma2 +
                " lambda2 = " + lambda2);

        System.out.println("beta3 = " + beta3 + " gamma3 = " + gamma3 +
                " lambda3= " + lambda3);

    }


}
