/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package CarreraCballos;

import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Carreracaballos {

    //Aqui declaramos las valiables compartidas y necesitamos inicializarlas lo hacemos. 
    //Variables compartidas(utilizadas) por mas de una clase Hilo ej: contadores, semaphoros...
    static int tablero;
    static int num_caballos;
    static int posicion_primero = 0;
    static char letra_ganador = '0';
    static Semaphore sem_pos_primero = new Semaphore(1);
    static Semaphore sem_ganador = new Semaphore(1);
    static boolean termina_carrera = false;

    static class Caballo extends Thread {

        char letra;
        int posicion;

        //Inicializar los valores necesarios del Hilo para que pueda ejecutar el run()
        Caballo(char l) {
            letra = l;
            posicion = 0;
        }

        //Accion que vaya a realizar toda instancia de esta clase == (AccionHilo_1 a1 = new AccionHilo_1())
        @Override
        public void run() {
            while (!termina_carrera) {
                try {
                    
                    //Ademas de hacer la accion,
                    //Debemos detectar la seccion critica y cercarla con semaphoros
                    //Tambien debemos pensar cual es la condicion de parada, ¿cuando deja de hacer la accion el hilo?
                    //Cada entrada a la seccion critica seria conveniente sacar un sout para controlar por consola la variable compartida
                    int dado = (int) (Math.random() * 6) + 1;
                    posicion += dado;
                    System.out.println("Caballo " + letra + " he sacado un " + dado + " voy en la posicion " + posicion);

                    sem_ganador.acquire();
                    if (letra_ganador == '0') {
                        sem_pos_primero.acquire();
                        if (posicion > posicion_primero) {
                            posicion_primero = this.posicion;
                            System.out.println("Me coloco en cabeza " + letra);
                        }
                        sem_pos_primero.release();
                    }
                    sem_ganador.release();

                    if (posicion >= tablero) {
                        sem_ganador.acquire();
                        termina_carrera = true;
                        if (letra_ganador == '0') {
                            letra_ganador = letra;
                            System.out.println("Caballo ganador: " + letra);
                        }
                        sem_ganador.release();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Carreracaballos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Dime tamaño tablero ");
        tablero = sc.nextInt();
        System.out.println("Dime cuantos caballos corren");
        num_caballos = sc.nextInt();

        Caballo[] caballos = new Caballo[num_caballos];
        char letra_caballo = 'A';
        for (int i = 0; i < num_caballos; i++) {
            caballos[i] = new Caballo(letra_caballo);
            letra_caballo++;
            caballos[i].start();
        }
        for (int i = 0; i < num_caballos; i++) {
            caballos[i].join();
        }

    }

}
