import java.util.regex.*;
import java.util.LinkedList;

public class RegexParser 
{
    /* Строка регулярного выражения */
    /* Читается как-то так: все подстроки начинающиеся с <a и не заканчивающиеся после начала на >. */
    /* Потом идут любое количество пробелов, которые оканчиваются на href="http://, за которым идут */
    /* Сколько угодно символов, которые обязательно закончаться при " */
    private final String A_HREF_STRING = "<a[^>]+href=\"http://(.+?)\"";

    private LinkedList<URLPair> urls; // Связный список найденных ссылок
    private Pattern urlPattern; // Паттерн для регулярного выражения

    public RegexParser()
    {
        this.urls = new LinkedList<>(); // Создаём пустой список
        this.urlPattern = Pattern.compile(A_HREF_STRING); // "Запекаем" паттерн
    }

    public LinkedList<URLPair> getUrlsFromPage(String page, int currentDepth)
    {
        this.urls.clear();  // Очищаем список ссылок; позволяет использовать много раз один и тот же экземпляр парсера

        if(page.equals("")) return this.urls;   // Если страница пустая - даже время нет смысла тратить

        int offset = 0; // Позволяет двигаться по строке дальше для поиска ссылок
        while(true)
        {
            /* Удобный тип для работы. */
            /* Здесь мы получаем контроль над частью страницы, где мы будем искать ссылки */
            StringBuilder pageBuilder = new StringBuilder(page.substring(offset)); 

            Matcher matcher = this.urlPattern.matcher(pageBuilder); // Находим по регулярному выражению тэг <a>
            if(matcher.find() == false) break;  // Если не нашли - то выходим из цикла, так как их на странице нет

            offset += matcher.end(); // Увеличиваем смещение по странице

            String newUrl = this.getFindedUrl(pageBuilder, matcher); // Вычленяем найденную ссылку

            this.urls.add(new URLPair(newUrl, currentDepth)); // Добавляем ссылку в список
        }

        return this.urls;
    }

    private String getFindedUrl(StringBuilder pageBuilder, Matcher matcher)
    {
        /* Получаем индекс, где начинается непосредственно ссылка */
        /* Эта функция возвращает индекс первого вхождения символов, т.е вернёт индекс "h", которое будет началом "href" */
        int startOfHref = pageBuilder.indexOf("href=\"", matcher.start());

        int start = startOfHref + 6; // Значит, надо сместить к началу нашей ссылки, то есть увеличить индекс начало на длину href=" -> 6
        int end = matcher.end() - 1; // -1 потому что паттерн захватывает символ "

        String findedUrl = pageBuilder.substring(start, end); // Вырезаем новую строку
        return findedUrl;
    } 
}