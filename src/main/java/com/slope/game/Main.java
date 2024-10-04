package com.slope.game;

class Main {
    private static Game game;

    public static void main(String[] args) throws Exception {
        boolean runApp = true;
        game = new Game();

        if (args.length > 0) {
            String mode = args[0];

            switch(mode) {
                case "workflowtest":
                    runApp = false;
            }
        }

        Engine.init();
        Engine.getMain().start();

        if(runApp) {
            try {
                Engine.getMain().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Engine.getMain().destroy();
    }

    public static Game getGame() {
        return game;
    }
}