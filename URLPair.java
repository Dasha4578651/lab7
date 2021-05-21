import java.net.URL;

public class URLPair
{
    private URL url;    // Встроенный класс, в разы упрощающий работу
    private int depth;  // Глубина текущей ссылки

    public URLPair(String urlString, int intDepth)
    {
        /* Не должно кидать ошибок, потому что парсер игнорит друге протоколы */
        try { this.url = new URL(urlString); }
        catch(Exception e) {}
        
        this.depth = intDepth;
    }

    public String getHost() { return this.url.getHost(); }
    public int getPort()
    {
        int port = this.url.getPort(); // Получаем порт через встроенную фукнцию; может вернуть -1, если порт не задан
        if(port == -1) return 80; // В этом случае возвращаем стандартный для HTTP порт 80

        return port;
    }
    public String getPath()
    {
        String path = this.url.getPath();   // Похожий прикол, что и в функции выше: может вернуть пустую строку, если path не задан
        if(path.equals("")) return "/"; // В этом случае надо вернуть обратную черту, которая означает "эта же страница"

        return path;
    }
    public String getFullUrl()
    {
        /* Конструируем ссылку обратно */
        /* У нас всегда будет http ссылки, так что я добваляю ей явно */
        String urlString = "http://" + this.url.getHost() + this.getPath(); 
        return urlString;
    }
    /* Функция, которая создаёт строковое представление данной пары ссылка-глубина */
    public String toString()
    {
        return String.format("[%s, %d]", this.getFullUrl(), this.depth);
    }
    
    /* Две функции, необходимые для работы HashSet */

    /* Возвращает уникальное число для объекта */
    /* Объекты разные - числа разные. Объекты одинаковые - числа одинаковые */
    /* От параметра глубины ничего не зависит, нас интересует только ссылка, поэтому необходим hashCode только для URL */
    public int hashCode() { return this.url.hashCode(); } 

    /* Проверяет равенство двух объектов */
    public boolean equals(Object o)
    {
        if(this == o) return true; // Если ссылка на объект - это он же, очвеидно - true
        if(o instanceof URLPair == false) return false; // Если сравниваемый объект - не экземпляр URLPair - false
        URLPair other = (URLPair)o; // Приводим сравниваемый объект к типу URLPair
        return this.url.equals(other.url);  // Выполняем сравнение при помощи встроенных URL, потому что только они представляют собой значимое различие
    }
}