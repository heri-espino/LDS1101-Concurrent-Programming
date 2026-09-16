public class Ejercicio02ProcesoHijo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("proceso hijo: inicia tarea externa");
        Thread.sleep(800);
        System.out.println("proceso hijo: termina tarea externa");
    }
}
