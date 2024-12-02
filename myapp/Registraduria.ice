
module Demo{
    interface File {
        void Share(string document);
    }

    interface QueryService {
        string consult(int document);
    }
    
    sequence<string> Task;

    interface Master {
        void receiveTask(Task tasks);
    }
}