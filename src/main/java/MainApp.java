import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class MainApp implements QuarkusApplication {

	public static void main(String... args) {
		Quarkus.run(MainApp.class, args);
	}

	@Override
	public int run(String... args) throws Exception {
		// Código que quieres ejecutar al iniciar
		System.out.println("Quarkus arrancó con clase main personalizada");

		// Mantener la aplicación corriendo
		Quarkus.waitForExit();
		return 0;
	}

}
