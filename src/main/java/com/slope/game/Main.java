package com.slope.game;

class Main {
    public static void main(String[] args) {
        boolean runApp = true;

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

        // Engine.getMain().destroy();
    }
}
