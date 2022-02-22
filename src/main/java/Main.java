import sendero.Gates;
import sendero.Merge;

public class Main {
    public static void main(String[] args) {
        Gates.In<String> hello = new Gates.In<>();
        Gates.In<String> world = new Gates.In<>("World");
        Merge<String[]> helloWorld = new Merge<>(new String[2], strings -> {
            boolean shouldDeliver = true;
            for (String s:strings
                 ) {
                if (s == null) {
                    shouldDeliver = false;
                    break;
                }
            }
            return shouldDeliver;
        });
        helloWorld.from(hello, updater -> helloString -> {
            updater.update(strings -> {
                String[] newRes = strings.clone();
                newRes[0] = helloString;
                return newRes;
            });
        });
        helloWorld.from(world, updater -> worldString -> {
            updater.update(strings -> {
                String[] newRes = strings.clone();
                newRes[1] = worldString;
                return newRes;
            });
        });

        Gates.Out.Single<String> result = helloWorld.forkMap(
                strings -> strings[0] + " " + strings[1]
        ).out(Gates.Out.Single.class);

        result.register(
                s -> {
                    System.out.println(s);
                    result.unregister();
                }
        );

        System.out.println("setting hello...");
        hello.accept("Hello");
    }
}
