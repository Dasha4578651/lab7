public class Main
{
    public static void main(String[] args)
    {
        if(args.length == 2)
        {
            /* Получаем первый агрумент и проверяем его, так как это единственное место, которое может подать неверную ссылку */
            String url = args[0];
            if(!url.startsWith("http://"))
            {
                System.out.print("Input correct full http url link!");
                return;
            }

            /* Получаем максимальную глубину, причём пользователь должен ввести строку, иначе нельзя запускать Crawler */
            int maxDepth = 0;
            try { maxDepth = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) 
            {
                System.out.println("Second arg is integer!");
                return;
            }

            /* Запускаем Crawler и выводим на экран посещённые сайты */
            Crawler crawler = Crawler.getCrawler(url, maxDepth);
            crawler.startCrawl();

            System.out.println();
            System.out.println(crawler.getVisited());
        }
        else
        {
            System.out.println("usage: <full url> <max depth>");
        }
    }
}