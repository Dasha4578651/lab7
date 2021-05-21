import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class HTTPRequest 
{
    public static int socketTimeout = 100; // Лимит по времени ожидания ответа от сервера в мс; можно поменять
    private Socket socket;  // Для работы с сетью
    private PrintWriter socketIn;   // Для отправки данных
    private BufferedReader socketOut;   // Для получения данных
    private URLPair url;    // Ссылка, которой запрос отправляется
    private String page;    // Содержимое страницы в виде строки

    /* Функция может выкинуть столько ошибок, потому что их отлавливать - не работа этого класса */
    public HTTPRequest(URLPair url) throws SocketException, IOException, UnknownHostException
    {
        this.page = ""; // Задаём страницу сначала пустой строкой
        this.url = url;

        this.socket = new Socket(this.url.getHost(), this.url.getPort()); // Создаём сокет и подключаемся к введённому хосту по введённому порту
        this.socket.setSoTimeout(socketTimeout);  // Ставим время лимит на время ответа
            
        this.socketIn = new PrintWriter(this.socket.getOutputStream(), true);   // Создаём поток записи
        this.socketOut = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));   // Создаём поток чтения

        this.sendRequest(); // Посылаем GET-запрос
        this.getPageFromServer();   // Считываем данные и сохраняем содержимое страницы
        this.close();   // Закрываем все открытые потоки
    }
    public String getPage() { return this.page; }

    private void sendRequest()
    {
        /* Тут объяснять нечего: GET как GET */
        this.socketIn.println("GET " + this.url.getPath() + " HTTP/1.1");
        this.socketIn.println("Host: " + this.url.getHost() + ":" + this.url.getPort());
        this.socketIn.println("Connection: Close");
        this.socketIn.println();
    }
    private void getPageFromServer()
    {
        /* Считываем первую строку в ответе сервера; если что пошло не так - выход из функции */
        String line = "";
        try { line = this.socketOut.readLine(); 
        } catch(Exception e) { return; }
        
        /* Если Statuc Code ответа не 200 (успешный) - то выходим из функции */
        if(line.indexOf("200") == -1) return;

        /* Пропускаем оставшийся заголовок - там ничего для нас интересного */
        /* Считываем до пустой строки, т.к PrintWriter считает, что "\r\n" == "" */
        try { while(!(line = this.socketOut.readLine()).equals(""));
        } catch (Exception e) { return; }

        /* Наконец мы добрались до самой страницы; считываем, пока можем */
        try { while((line = this.socketOut.readLine()) != null) { this.page += line; } 
        } catch(Exception e) {}
    }
    private void close()
    {
        // Просто закрытие всех потоков, ничего необычного
        try
        {
            this.socket.close();
            this.socketOut.close();
            this.socketIn.close();
        } catch(Exception e) {}
    }
}
