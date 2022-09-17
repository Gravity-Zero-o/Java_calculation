//181911727 高源 强度作业 1-1
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.lang.Math;
import java.util.Scanner;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class input {
    static double[] x = new double[6];
    static double[] y = new double[6];
    static double[] z = new double[6];
    static double[] A = new double[6];
    static double[] alpha = new double[6];
    static double[][] a = new double [5][6];//tmp
    static double[][] b = new double [8][5];//tmp
    static double[] deltaP = new double [5];
    static double[] CenStress = new double [6];
    static double[] AeroForceX = new double [5];
    static double[] AeroForceY = new double [5];
    static double[] CenForceY = new double [5];
    static double[] CenForceZ = new double [5];

    static double[] AeroForceMomentX = new double [6];
    static double[] AeroForceMomentY = new double [6];
    static double[] CenForceMomentX = new double [6];
    static double[] CenForceMomentY = new double [6];
    static double[] TotalMomentX = new double [6];
    static double[] TotalMomentY = new double [6];
    static double[] TotalMomentYipuxid = new double [6];//how many?
    static double[] TotalMomentYita = new double [6];
    static double[] J_yita = new double [5];
    static double[] J_yipuxid = new double [5];
    static double[] YitaA = new double [5];
    static double[] YitaB = new double [5];
    static double[] YitaC = new double [5];
    static double[] YipuxidA = new double [5];
    static double[] YipuxidB = new double [5];
    static double[] YipuxidC = new double [5];
    static double[] SigmaA=new double [6];//Bending stress
    static double[] SigmaB=new double [6];
    static double[] SigmaC=new double [6];
    static double[] TotalStressA=new double [6];
    static double[] TotalStressB=new double [6];
    static double[] TotalStressC=new double [6];
    static int omega=4700;//rpm
    static int rho_iron=8200;//kg/m^3
    static int C1am = 297, C1um = 410,C2am=313,C2um=-38;//m/s
    static double p1=0.222,p2=0.178;//MPa
    static double rho1=0.894,rho2=0.75;//kg/m^3
    static int Q= 68;//number of blades

    public static void main(String[] args) throws FileNotFoundException {
        @SuppressWarnings("InfinirteLoopStatement")
        //this aiming to read data from table 1-3
        int m = 0;
        double [] c = new double[30];//read data from Table 1-3
        InputStream list = new FileInputStream("E://3data.txt");
        Scanner scanner = new Scanner(list);
        while (scanner.hasNextDouble()) {
            double num = scanner.nextDouble();
            c[m] = num;//read all
            m++;
        }
        /*for(int i=0;i<30;i++) {
            System.out.printf("%.2f", c[i]);
            System.out.printf(" ");
        }*/
        //this is aiming to read data from table 1-3
        int n = 0;
        double [] d = new double[40];//read data from Table 1-4
        InputStream list1 = new FileInputStream("E://4data.txt");
        Scanner scanner1 = new Scanner(list1);
        while (scanner1.hasNextDouble()) {
            double num1 = scanner1.nextDouble();
            d[n] = num1;//read all
            n++;
        }
        /*for(int i=0;i<40;i++) {
            System.out.printf("%.3f", d[i]);
            System.out.printf(" \n");
        }*/
        //this is aming to match the data from table 1-4
        for(int p=0;p<5;p++){
            for(int q=0;q<6;q++){
                a[p][q] = c[p * 6 + q];
                  System.out.printf("%.2f", a[p][q]);
                  if(q==5) System.out.print("\n");
                 System.out.print(" ");
            }
        }
        for(int i=0;i<6;i++){
            x[i]= a [0][i];
            y[i]= a [1][i];
            z[i]= a [2][i];
            A[i]= a [3][i];
            alpha[i]= a [4][i];//match the number
        }
//this is aming to match the data from table 1-4
        for(int P=0;P<8;P++){
            for(int M=0;M<5;M++){
                b[P][M] = d[P * 5 + M];
                /*System.out.printf("%.3f", b[P][M]);
                System.out.print(" ");*/
            }
        }
        for(int i=0;i<5;i++){
            J_yita[i]   = b [0][i];
            J_yipuxid[i]= b [1][i];
            YitaA[i]    = b [2][i];
            YipuxidA[i] = b [3][i];
            YitaB[i]    = b [4][i];
            YipuxidB[i] = b [5][i];
            YitaC[i]    = b [6][i];
            YipuxidC[i] = b [7][i];
            //match the number
        }
       // System.out.printf("%.3f", alpha[5]);//test
        CentrifugalStress();
        System.out.print("\n");
        Aerodynamic_BendingMoment();
        System.out.print("\n");
        Centrifugal_BendingMoment();
        System.out.print("\n");
        Total_BendingMoment();
        System.out.print("\n");
        BendingStress();
        System.out.print("\n");
        TotalStress();
        System.out.print("\n");
        //out put txt
        System.out.println("");//is the \n in the end of console
        FileOutputStream fop = null;
        File file;
        try {
            file = new File("E://out.txt");
            fop = new FileOutputStream(file);
            // if not , create one
            if (!file.exists()) {
                file.createNewFile();
            }

            //OutputStreamWriter dos1=new OutputStreamWriter(fop);//maybe useless
            fop.write("***************************************************** \n".getBytes());
            fop.write("*THIS IS AN OUTPUT FILE OF A PROGRAM DESIGNED BY G.Y.*\n".getBytes());
            fop.write("* WHICH IS AIMING TO CALCULATE THE STRESS AND MOMENT *\n".getBytes());
            fop.write("*THE INITIAL DATA COMES FROM THE TEXTBOOK 1-3 AND 1-4* \n".getBytes());
            fop.write("****************************************************** \n".getBytes());
            fop.write("NUMBER OF PLATE       I        II        III       IV          V \n".getBytes());
                String space = "     ";
                String line ="";

            fop.write("Cen_Stress(MPa)     ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String ans_1 = decimalFormat.format(CenStress[i]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                //String name="x"+(i+1)+"=";//if we need some parameter_name front of number
                //String space = "   ";
                //String line ="";
                //if((i+1)%6==0) line+="\r\n";
                //byte[] bytes1 = name.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                //fop.write(bytes1);
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);

            }
            fop.write("\nAeroMomentX(N*M)     ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(AeroForceMomentX[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }

            fop.write("\nAeroMomentY(N*M)  ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(AeroForceMomentY[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }

            fop.write("\nCenMomentX(N*M)     ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(CenForceMomentX[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nCenMomentY(N*M)     ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(CenForceMomentY[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nTotalMomentX(N*M)    ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(TotalMomentX[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nTotalMomentY(N*M)    ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(TotalMomentY[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nSigma_A(MPa)        ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(SigmaA[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nSigma_B(MPa)        ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(SigmaB[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nSigma_C(MPa)        ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(SigmaC[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nTotalStress_A(MPa)  ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(TotalStressA[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nTotalStress_B(MPa)  ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(TotalStressB[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }
            fop.write("\nTotalStress_C(MPa)  ".getBytes());
            for(int i=0;i<5;i++) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String ans_1 = decimalFormat.format(TotalStressC[i+1]);
                String strDouble =String.valueOf(ans_1);
                byte[] contentInBytes1 = strDouble.getBytes();
                byte[] bytes2 = space.getBytes();
                byte[] bytes3 = line.getBytes();
                fop.write(contentInBytes1);
                fop.write(bytes2);
                fop.write(bytes3);
            }

            // turn to byte
            fop.flush();
            fop.close();
            System.out.println("ALL IS DONE,THANK YOU~");
        } catch (IOException e) { e.printStackTrace(); } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
public static void CentrifugalStress(){
        double w = 2*Math.PI*omega/60;
        for(int i=0;i<5;i++){
            deltaP[i] = rho_iron*w*w*(A[i]+A[i+1])/2*(z[i]+z[i+1])/2*(z[i]-z[i+1])*0.0001*0.01*0.01;

            /*System.out.print("The deltaP of part "+(i+1)+" is ");
            System.out.printf("%.2f", deltaP[i]);
            System.out.print(" N \n");*/
        }
        for(int i=0;i<5;i++){//i plate
            double tmp=0;
            for(int j=0;j<=i;j++){
                tmp += deltaP[j];
            }
            CenStress[i]=tmp/A[i+1]*0.01;// the next plate
            /*System.out.print("deltap is ");
            System.out.print(deltaP[i]);*/
            /*System.out.printf("The "+(i+1)+" plate censtrees is ");
            System.out.printf("%.2f", CenStress[i]);
            System.out.printf(" MPa"+"\n");*/
        }
}
public static void Aerodynamic_BendingMoment(){
        for(int i=0;i<5;i++){
            AeroForceX[i]=2*Math.PI/Q*(z[i]+z[i+1])/200*((rho1*C1am*C1am-rho2*C2am*C2am)+(p1-p2)*1000000)*(z[i]-z[i+1])/100;
            AeroForceY[i]=2*Math.PI/Q*(z[i]+z[i+1])/200*(rho1*C1am*C1um-rho2*C2am*C2um)*(z[i]-z[i+1])/100;
            System.out.print("The AeroForce X of part "+(i+1)+" is ");
            System.out.printf("%.2f", AeroForceX[i]);
            System.out.print("  and Y is ");
            System.out.printf("%.2f", AeroForceY[i]);
            System.out.print(" N \n");
        }
        for(int j=1;j<6;j++) {
            double tmp  = 0;
            double tmp1 = 0;
            for(int i=0;i<j;i++) {
                tmp  += AeroForceY[i]*((z[i]+z[i+1])/2-z[j])/100;
                tmp1 += AeroForceX[i]*((z[i]+z[i+1])/2-z[j])/100*(-1);
            }
            AeroForceMomentX[j]=tmp;
            AeroForceMomentY[j]=tmp1;
            System.out.print("The AeroForceMomentX of plate "+j+" is ");
            System.out.printf("%.2f", AeroForceMomentX[j]);
            System.out.print(" and Y is ");
            System.out.printf("%.2f", AeroForceMomentY[j]);
            System.out.print(" N*M \n");
        }
}
public static void Centrifugal_BendingMoment(){
    double w = 2*Math.PI*omega/60;
        for(int i=0;i<5;i++){
            CenForceY[i]=rho_iron*w*w*(A[i]+A[i+1])*(z[i]-z[i+1])/2000000*(y[i]+y[i+1])/200;
            CenForceZ[i]=rho_iron*w*w*(A[i]+A[i+1])*(z[i]-z[i+1])/2000000*(z[i]+z[i+1])/200;
        }
    for(int j=1;j<6;j++) {
        double tmp = 0;
        double tmp1 = 0;
        for(int i=0;i<j;i++) {
            tmp  += CenForceY[i]*((z[i]+z[i+1])/2-z[j])/100-CenForceZ[i]*((y[i]+y[i+1])/2-y[j])/100;
            tmp1 += CenForceZ[i]*((x[i]+x[i+1])/2-x[j])/100;
        }
        CenForceMomentX[j]=tmp;
        CenForceMomentY[j]=tmp1;
        System.out.print("The CenForceY of plate "+j+" is ");
        System.out.printf("%.2f", CenForceY[j-1]);
        System.out.print(" and Z is ");
        System.out.printf("%.2f", CenForceZ[j-1]);
        System.out.print("\n ");
        //System.out.print("\nThe CenForceMomentX of plate "+j+" is ");
        //System.out.printf("%.2f", CenForceMomentX[j]);
        //System.out.print(" ");

        /*for(int i=0;i<j;i++) {
            tmp1 += CenForceZ[i]*((x[i]+x[i+1])/2-x[j])/100;
        }
        CenForceMomentY[j]=tmp1;*/
        //System.out.print("and Y is ");
        //System.out.printf("%.2f", CenForceMomentY[j]);
        //System.out.print(" N*M \n");
    }
}
    public static void Total_BendingMoment(){
        for(int i=0;i<6;i++){
            TotalMomentX[i]=AeroForceMomentX[i]+CenForceMomentX[i];
            TotalMomentY[i]=AeroForceMomentY[i]+CenForceMomentY[i];
            System.out.print("The TotalMomentY of plate "+i+" is ");
            System.out.printf("%.2f", TotalMomentY[i]);
            System.out.print(" N*M \n");
        }
    }
    public static void BendingStress(){
        for(int i=1;i<6;i++){
            TotalMomentYita[i]=TotalMomentX[i]*Math.cos(alpha[i])-TotalMomentY[i]*Math.sin(alpha[i]);
            TotalMomentYipuxid[i]=TotalMomentX[i]*Math.sin(alpha[i])+TotalMomentY[i]*Math.cos(alpha[i]);
            SigmaA[i]=TotalMomentYipuxid[i]/J_yipuxid[i-1]*YitaA[i-1]-TotalMomentYita[i]/J_yita[i-1]*YipuxidA[i-1];
            SigmaB[i]=TotalMomentYipuxid[i]/J_yipuxid[i-1]*YitaB[i-1]-TotalMomentYita[i]/J_yita[i-1]*YipuxidB[i-1];
            SigmaC[i]=TotalMomentYipuxid[i]/J_yipuxid[i-1]*YitaC[i-1]-TotalMomentYita[i]/J_yita[i-1]*YipuxidC[i-1];
            System.out.print("Sigema_A "+i+" is ");
            System.out.printf("%.2f", SigmaA[i]);
            System.out.print("  \n");
        }
    }
    public static void TotalStress(){
           for(int i=1;i<6;i++){
               TotalStressA[i]=SigmaA[i]+CenStress[i-1];
               TotalStressB[i]=SigmaB[i]+CenStress[i-1];
               TotalStressC[i]=SigmaC[i]+CenStress[i-1];
           }
    }
}
