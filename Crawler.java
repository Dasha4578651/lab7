import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

/** 
 * Синглтон для класса Crawler
 * Зачем - да просто так 
 */
public class Crawler 
{
    private static Crawler instance = null; // На старте программы у нас нет экземпляра Crawler

    private RegexParser crawlerParser;  // Для поиска всех <a> тегов
    private HashSet<URLPair> visited;   // Контейнер для посещённых сайтов
    private Queue<URLPair> toVisit;     // Контейнер для сайтов, который надо посетить
    private int maxDepth;               // Максимальная глубина обхода для Crawler
    
    /* Это функция для паттерна Синглтон - в прогремме может быть только один Crawler. */
    /* Вручную мы его создать не можем - конструктор private, а значит, только получить ссылку на существующий. */
    public static Crawler getCrawler(String firstUrl, int depth)
    {
        if(instance == null)
            instance = new Crawler(firstUrl, depth);
        return instance;
    }
    private Crawler(String firstUrl, int depth)
    {
        this.maxDepth = depth;                  // устанавливаем максимальную глубину

        this.crawlerParser = new RegexParser(); // штатный парсер для страницы
        this.visited = new HashSet<>();         // HashSet - контейнер, где хранятся уникальные экземпляры; все операции за О(1)
        this.toVisit = new LinkedList<>();      // Queue - это интефейс, LinkedList - собственно контейнер

        this.toVisit.add(new URLPair(firstUrl, 0)); // Добавляем первую ссылку в очередь для обработки
    }

    public void startCrawl()
    {
        int currentDepth = 0;       // текущая глубина; как обошли всех в очереди на предыдущем шаге - увеличить на 1
        int remainUrls = 1;         // Т.к мы добавили одну ссылку, то это и будет длина очереди вначале
        /* Если наша очередь на проверку оказалась пуста или мы достигли максимальной глубины, то остановить цикл */
        while(remainUrls > 0 && currentDepth < this.maxDepth)   
        {
            /* Получив количество ссылок в очереди, мы проходимся ровно по ним. */
            /* Таким образом, когда мы завершили обход, в очереди останутся только */
            /* ссылки для следующей глубины. */
            for(int i = 0; i < remainUrls; i++)
            {
                URLPair urlToVisit = this.toVisit.poll(); // Вытягиваем из очереди с начала ссылку
 
                /* Если мы уже проходили по ней ранее - переходим к следующей */
                /* В ином случае - если не проходили - добавляем к посещённым */
                if(this.visited.contains(urlToVisit))   continue;
                this.visited.add(urlToVisit);

                /* Функция, которая выполняет обход одного сайта; сделала для визуальной чистоты кода */
                this.crawlOne(urlToVisit, currentDepth);
            }
            currentDepth++;     // Мы обошли все сайты из глубины n, пора глубину увеличить
            remainUrls = this.toVisit.size();   // Получаем новое значение количества сайтов, ожидающих посещения
        }
    }
    public HashSet<URLPair> getVisited()
    {
        return this.visited;
    }

    /* Функция, которая обрабатывает один сайт за раз */
    private void crawlOne(URLPair url, int currentDepth)
    {
        try 
        {
            HTTPRequest request = new HTTPRequest(url); // Отправляем запрос на сервер
            String page = request.getPage();    // Получаем страницу

            System.out.println("Parsing: " + url);

            /* Парсим полученную страницу и сохраняем значения в массиве */
            LinkedList<URLPair> urlsToAdd = this.crawlerParser.getUrlsFromPage(page, currentDepth + 1);

            this.toVisit.addAll(urlsToAdd); // Добавляем все полученные ссылки в очередь
        } catch (Exception e)
        { System.out.println("Error in crawling " + url + " " + e); }
    }
}