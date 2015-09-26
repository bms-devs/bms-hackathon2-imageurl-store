/**
 * J I T A C  -  Image to ASCII Converter
 * (c) 2001,2002 by Konrad Rieck <kr@roqe.org> - http://www.roqe.org/jitac 
 */
package org.bmshackathon.ascii;

import org.roqe.jitac.*;

import java.util.Vector;
import java.io.*;
import java.net.URL;

/** 
 * Jitac - Image to ASCII Converter Main Class.
 * 
 * @version $Id: Jitac.java,v 1.17 2002/02/01 22:27:10 kr Exp $
 * @author Konrad Rieck
 */
public class Jitac {
  
   public final static String VERSION="0.2.0";
   public final static int COLOR_RED=1;
   public final static int COLOR_GREEN=2;
   public final static int COLOR_BLUE=4;

   private Image img;
   private FixedFont ff;
   private int imgWidth, imgHeight, ffWidth, ffHeight; 
   private Vector weigths;

   /**
    * Construct a Jitac object.
    * @param i Image
    * @param f FixedFont
    * @param w Weight vektors
    */
   public Jitac(Image i, FixedFont f, Vector w) {
      img=i;
      imgWidth=img.getWidth();
      imgHeight=img.getHeight();
      
      ff=f;   
      ffWidth=ff.getWidth();
      ffHeight=ff.getHeight();
      
      weigths=w;
   }

   /**
    * Calculate the distance between two vektors. Noise can be added if
    * necessary, the distance is calculated using as the euklidian 
    * distance.
    * @param v Vektor
    * @param n noise
    * @return index of the nearest weight
    */
   public int findNearestEuklid(Vektor v, double noise) throws JitacException {
      Vektor ret=null, w=null; 
      double min=1000000;
      double d=0;
      
      for(int i=0;i<weigths.size();i++) {
         w=(Vektor)weigths.get(i);
         w=w.addNoise(noise);
         d=w.sub(v).length();
         if(d<min) {
            ret=w;
            min=d;
         }
      }
      return ret.getIdentifier();
   }

   /** 
    * Main conversion loop.
    * @param out Outputstream
    * @param round Round values
    * @param verbose Verbose-level
    * @param noise Noise-level
    * @param withHTML Convert charakters for HTML output
    * @param withColorHTML With HTML colors
    * @param smooth Smooth colors
    * @param inColorMask input color mask
    * @param outColorMask output color mask
    */
   public void convert(PrintStream out, boolean round, boolean verbose, 
                       double noise, boolean withHTML, boolean withColorHTML,
                       boolean smooth, int inColorMask, int outColorMask) 
                       throws JitacException {
      char c;
      Vektor v;
      String s, colorStr;
      
      if(verbose)
         System.err.print(" - Converting:        ");
      
      for(int i=0;i<imgHeight;i+=ffHeight) {
         for(int j=0;j<imgWidth;j+=ffWidth) {
            v=img.getVektor(j,i,ffWidth,ffHeight,round,inColorMask);
            
            c=(char)findNearestEuklid(v,noise);
            
            if(withHTML || withColorHTML) 
               s=createHTMLEntities(c);
            else
               s=c+"";
            
            if(withColorHTML) {
               colorStr=img.getColorStr(j,i,ff.getGlyph(c),smooth,
                                        outColorMask);
               s="<font color=\""+colorStr+"\">"+s+"</font>";
            }
                  
            out.print(s);
         }
         
         if(withHTML || withColorHTML)
            out.println("<br>");
         else
            out.println();
         
         
         if(verbose && imgHeight>50 && (i%(imgHeight/50))==0)
            System.err.print(".");
      }
      if(verbose)
         System.err.println("Done.");   
   }

   private String createHTMLEntities(char c) {
      String s;
      
      switch(c) {
         case '<': 
            s="&lt;";
            break;
         case '>':
            s="&gt;";
            break;
         case ' ':
            s="&nbsp;";
            break;
         case '&':
            s="&amp;";
            break;
         case '"':
            s="&quot;";
            break;   
         default:
            s=c+"";
      }
      
      return s;
   }

   /**
    * Print the README.
    * @param path Path to README
    */
   public static void printREADME(URL path) throws JitacException {
      try {
         BufferedReader r = new BufferedReader(new InputStreamReader(
                                                path.openStream()));      
         while(r.ready()) {
            System.err.println(r.readLine());
         }
         System.exit(0);
                                                         
      } catch(Exception e) {
         throw(new JitacException(e.getMessage()));
      }                                                
   }

   /** 
    * Print version information.
    */
   public static void printVersion() {
      System.err.println("\n"+
"Jitac v"+VERSION+" - Image to ASCII Converter, http://www.roqe.org/jitac\n"+
"    Copyright (c) 2001,2002 by Konrad Rieck <kr@roqe.org>, the Roqefellaz\n\n"+ 
"This file also includes:\n"+
"  - The Java Image Manipulation Interface (Jimi)\n"+
"    Copyright (c) 2001 by Sun Microsystems, Inc.\n"
      );
      System.exit(0); 
   }

   /** 
    * Print the integrated font list. 
    * @param path path to a font inside the jar
    */
   public static void printFontList(URL path) throws JitacException {
      /*
       * Forget it! Something is wrong, well, for now we make
       * this list hard coded. XXX
       */
      
      System.err.println("\nAvailable integrated fonts:\n");
      System.err.println("   courier08");
      System.err.println("   courier10");
      System.err.println("   courier12");
      System.err.println("   fixed05x08");
      System.err.println("   fixed07x14");
      System.err.println("   fixed08x16");
      System.err.println("   fixed10x20");
      System.err.println("   vga08x09");
      System.err.println("   vga08x16\n");

      System.exit(0);
   }
   
   /**
    * Print usage.
    */
   public static void printUsage() {
      System.err.println(
"usage: jitac [-hirvRVHC] [-g <g>] [-s <w>x<h>] [-S <w>x<h>] [-o <file>]\n"+
"             [-w <w>] [-n <n>] [-l <fn> | -f <fn>] [-x <s>-<e> | -X <str>]\n"+
"             [-c <m>] [-P <m>] <file>\n"+
"options:\n"+
" -h             Print this help screen.\n"+
" -i             Invert source image.\n"+
" -v             Be verbose during conversion.\n"+
" -V             Print version and copyright information.\n"+
" -r             Round input vectors to 0 or 1. (Default: 0-1)\n"+
" -R             Print README file.\n"+
" -g <g>         Apply gamma value <g> to the source image. (Default: 1.0)\n"+
" -c <m>         Input color channel mask, red=1, green=2, blue=4\n"+
" -n <n>         Add noise to weight vectors. (Default: 0.0)\n"+
" -s <w>x<h>     Resize source image to geometry <w> x <h>.\n"+
" -S <w>x<h>     Scale source image width by <w> and height by <h>.\n"+
" -o <file>      Write ASCII image to <file>. (Default: stdout)\n"+
" -w <w>         Create ASCII image with a charakter width of <w>.\n"+
" -l <url>       Load a BDF font from URL <url>, e.g. file:fonts/test.bdf\n"+
" -f <fn>        Use the integrated font <fn>. (Default: courier10).\n"+
"                Use -L to get a list of integrated fonts\n"+
" -x <s>-<e>     Use charakters from <s> to <e>. Values must be integers.\n"+
"                (Default: 32 - 126)\n"+
" -X <str>       Use only charakters from the String <str>.\n"+         
" -H             Create HTML output\n"+
" -C             Create colored HTML output\n"+
" -M             Smooth color values in colored HTML output (-C)\n"+
" -I             Invert HTML background in HTML output (-C or -H)\n"+
" -P <m>         Output color channel mask, red=1, green=2, blue=4 (-C)\n"+
"supported image formats:\n"+
" GIF, JPEG, TIFF, PNG, PICT, Photoshop, BMP, Targa, ICO, CUR, Sunraster, XBM,\n"+
" XPM, and PCX."
      );
      System.exit(0);
   }
   
   public static void start(String[] args) throws JitacException {
      boolean hOpt=false, iOpt=false, rOpt=false, LOpt=false, cOpt=false,
              gOpt=false, sOpt=false, SOpt=false, oOpt=false, lOpt=false,
              fOpt=false, wOpt=false, vOpt=false, VOpt=false, xOpt=false,
              XOpt=false, nOpt=false, HOpt=false, COpt=false, MOpt=false,
              IOpt=false, POpt=false; 
      double gamma=1.0, wScale=1.0, hScale=1.0, noise=0; 
      int imgWidth=0, imgHeight=0, charWidth=80, charStart=32, charEnd=126, c;
      int inColorMask=0, outColorMask=COLOR_RED|COLOR_GREEN|COLOR_BLUE;
      String iFont="courier10", lFont=null, outFile=null, chars=null, tmp;
      String fontPath;
      URL font;
      PrintStream ps;
      Vector inputs; 
      Class me=null;
   
      try {
         me=Class.forName("org.roqe.jitac.Jitac");
      } catch(Exception e) {
         throw(new JitacException("Could not find myself!?"));
      }   

      font=me.getResource("fonts/"+iFont+".bdf");
   
      Getopt g = new Getopt("jitac", args,
                            "c:f:g:hil:n:o:rs:vw:x:S:LRVX:CHMIP:");   
   
      while((c=g.getopt())!=-1) {
         switch(c) {
            case 'f':
                fOpt=true; 
                iFont=g.getOptarg(); 
                font=me.getResource("fonts/"+iFont+".bdf");
                break;
            case 'g':
               gOpt=true;
               gamma=Double.parseDouble(g.getOptarg());   
               break;
            case 'i':
               iOpt=true;
               break;
            case 'n':
               nOpt=true;
               noise=Double.parseDouble(g.getOptarg());   
               break;
            case 'l':
               lOpt=true;
               lFont=g.getOptarg();
               break;
            case 'o':
               oOpt=true;
               outFile=g.getOptarg();         
               break;
            case 'r':
               rOpt=true;
               break;
            case 's':
               sOpt=true;
               tmp=g.getOptarg();
               if(tmp.indexOf('x')<1 || tmp.indexOf('x')>tmp.length()-1)
                  printUsage();
               imgWidth=Integer.parseInt(tmp.substring(0,tmp.indexOf('x')));
               imgHeight=Integer.parseInt(tmp.substring(tmp.indexOf('x')+1,
                                         tmp.length()));   
               break;      
            case 'S':
               SOpt=true;
               tmp=g.getOptarg();
               if(tmp.indexOf('x')<1 || tmp.indexOf('x')>tmp.length()-1)
                  printUsage();
               wScale=Double.parseDouble(tmp.substring(0,tmp.indexOf('x')));
               hScale=Double.parseDouble(tmp.substring(tmp.indexOf('x')+1,
                                         tmp.length()));   
               break;  
            case 'c':
               cOpt=true;
               inColorMask=Integer.parseInt(g.getOptarg());
               break;       
            case 'P':
               POpt=true;
               outColorMask=Integer.parseInt(g.getOptarg());
               break;       
            case 'v': 
               vOpt=true;
               break;
            case 'w':
               wOpt=true;
               charWidth=Integer.parseInt(g.getOptarg());
               break;   
            case 'x':
               xOpt=true;
               tmp=g.getOptarg();
               if(tmp.indexOf('-')<1 || tmp.indexOf('-')>tmp.length()-1)
                  printUsage();
               charStart=Integer.parseInt(tmp.substring(0,tmp.indexOf('-')));
               charEnd=Integer.parseInt(tmp.substring(tmp.indexOf('-')+1,
                                        tmp.length()));   
               break;
            case 'X':
               XOpt=true;
               chars=g.getOptarg();
               break;
            case 'C':
               COpt=true;
               break;
            case 'I':
               IOpt=true;
               break;
            case 'M':
               MOpt=true;
               break;               
            case 'H':
               HOpt=true;
               break;
            case 'R':
               printREADME(me.getResource("README"));     
            case 'L':
               printFontList(font);
            case 'V':
               printVersion();      
            default:   
            case 'h':
               printUsage();
               
         }      
      }
   
      if(args.length-g.getOptind()!=1) 
         printUsage();
      
      if(fOpt && lOpt) {
         System.err.println("You can not specify -l and -f.");
         System.exit(0);
      } 

      if(xOpt && XOpt) {
         System.err.println("You can not specify -x and -X.");
         System.exit(0);
      } 

      if(!COpt && MOpt) { 
         System.err.println("You can use -M only with -C."); 
         System.exit(0);
      } 

      if(!COpt && POpt) { 
         System.err.println("You can use -P only with -C."); 
         System.exit(0);
      } 

      if(!COpt && !HOpt && IOpt) {
         System.err.println("You can use -I only with -C or -H.");
         System.exit(0);
      } 

      
      try {
         if(lOpt) 
            font=new URL(lFont);
      } catch(Exception e) {
         throw(new JitacException(e.getMessage()));
      }        
      
      if(font==null)
         throw(new JitacException("Could not find font."));
      
      if(vOpt)
         System.err.println("\nLoading");
      
      FixedFont ff=BDFFont.load(font,vOpt); 
      
      Image img=new Image(args[g.getOptind()],vOpt);
      
      if(!COpt && inColorMask==0)
         img.gray();
         
      if(gOpt) 
         img.gamma(gamma);
         
      if(iOpt)
         img.invert();
      
      if(SOpt) 
         img.scale(wScale, hScale);
         
      if(sOpt)
         img.resize(imgWidth,imgHeight);
         
      if(wOpt) {         
         double sc=((double)charWidth*ff.getWidth())/img.getWidth();
         img.scale(sc,sc);
      }   
      
      
      if(oOpt) {
         try {
            ps=new PrintStream(new FileOutputStream(outFile));
         } catch(IOException e) {
            throw(new JitacException(e.getMessage()));
         }   
      } else
         ps=System.out;   
      
      
      if(HOpt || COpt) {
         ps.print(
         "<!doctype html public \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n"+
         "                       \"http://www.w3.org/TR/html4/loose.dtd\">\n"+
         "<html>\n\n"+
         "<head>\n"+
         "<title>Jitac Output for \""+args[g.getOptind()]+"\"</title>\n"+
         "</head>\n\n"
         );
         
         if(iOpt || IOpt)
            ps.println("<body bgcolor=\"black\" text=\"white\">");
         else   
            ps.println("<body bgcolor=\"white\" text=\"black\">");
            
         ps.println("<tt>\n<font size=\"2\">");   
      }
      
      if(XOpt) 
         inputs=ff.getVektors(chars);
      else
         inputs=ff.getVektors(charStart,charEnd);
         
      if(vOpt) {
         System.err.println("\nConversion");
         System.err.println(" - Source image:      "+args[g.getOptind()]);
         System.err.println(" - Output file:       "+(oOpt?outFile:"stdout"));
         System.err.println(" - Font:              "+(lOpt?lFont:iFont));
         System.err.println(" - Gamma:             "+gamma);
         System.err.println(" - Grayscale:         "+!cOpt);
         System.err.println(" - Input color mask:  "+inColorMask);
         System.err.println(" - Output color mask: "+outColorMask);
         System.err.println(" - Noise:             "+noise);
         System.err.println(" - Image inverted:    "+iOpt);
         System.err.println(" - Vektor rounded:    "+rOpt);
         System.err.println(" - HTML conversion:   "+(HOpt||COpt));
         System.err.println(" - Colored HTML:      "+COpt); 
         System.err.println(" - Smooth color:      "+MOpt);
         System.err.println(" - Back inverted:     "+IOpt);
         System.err.println(" - Image Dimension:   "+img.getWidth()+" x "+
                                                     img.getHeight());
         
         if(SOpt) 
            System.err.println(" - Image Dimension:   "+wScale+" x "+
                                                      hScale);
         
         if(XOpt)
            System.err.println(" - Character range:   {"+chars+"}");
         else
            System.err.println(" - Character range:   "+charStart+" - "+charEnd);
            
         if(wOpt)
            System.err.println(" - Character width:   "+charWidth);
      }   
      
      Jitac j=new Jitac(img,ff,inputs);
      j.convert(ps, rOpt, vOpt, noise, HOpt,COpt,MOpt,inColorMask, 
                outColorMask);
    
      if(HOpt || COpt) 
        ps.print("\n</tt>\n</font>\n</body>\n\n</html>\n");
      
      if(oOpt)
        ps.close();
      
//      System.exit(0);
   }
}