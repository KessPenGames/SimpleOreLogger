package net.simple.orelogger.config;

import org.bukkit.Material;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigName("config.yml")
public interface MainConfig extends Config {
    @Comment("Включить плагин")
    default boolean getEnablePlugin() {
        return false;
    }

    @Comment("IP адресс от MySql")
    default String getMysqlIp() {
        return "127.0.0.1";
    }
    @Comment("Порт от MySql")
    default String getMysqlPort() {
        return "3306";
    }
    @Comment("Название Базы Данных")
    default String getDbName() {
        return "name";
    }
    @Comment("Пользователь Базы Данных")
    default String getDbUser() {
        return "user";
    }
    @Comment("Пароль пользователя Базы Данных")
    default String getDbPassword() {
        return "password";
    }
    @Comment("Название таблицы в Базе Данных")
    default String getDbTable() {
        return "table";
    }

    @Comment("Обновление данных в базе данных (миллисекунды)")
    default int getDatabaseUpdateMilliseconds() {
        return 60000;
    }

    @Comment("Список блоков которые будут проверятся")
    default List<String> getListOre() {
        return Arrays.asList(
                Material.STONE.toString(),
                Material.DIAMOND_ORE.toString(),
                Material.DEEPSLATE_DIAMOND_ORE.toString(),
                Material.NETHERRACK.toString(),
                Material.ANCIENT_DEBRIS.toString()
        );
    }

    @Comment("Сообщение о том что у игрока не хватает прав для команды")
    default String getNotPermissionMessage() {
        return "&4У вас не хватает прав для данной команды!";
    }
    @Comment("Сообщение о том что игрока нет нужных аргументов в команде")
    default String getNotArgumentsMessage() {
        return "&4Вы ввели не правильные аргументы для команды!";
    }
    @Comment("Сообщение о том что игрок не найден")
    default String getPlayerNotFoundMessage() {
        return "&4Игрок не найден!";
    }

    @Comment("Цвета блоков в чате")
    default Map<String, String> getOreColor() {
        Map<String, String> map = new HashMap<>();
        map.put(Material.STONE.toString(), "&7");
        map.put(Material.DIAMOND_ORE.toString(), "&b");
        map.put(Material.DEEPSLATE_DIAMOND_ORE.toString(), "&b");
        map.put(Material.NETHERRACK.toString(), "&4");
        map.put(Material.ANCIENT_DEBRIS.toString(), "&#713c00");
        return map;
    }

    @Comment("Верхняя часть сообщения статистики")
    default List<String> getStatisticHeader() {
        return Arrays.asList("&eСтатистика за %day% дней", "&bСтатистика игрока: %player%");
    }
    @Comment("Паттерн сообщения статистики о добытой руде")
    default String getStatisticOre() {
        return "%ore_color%%ore_type% &r- %count%";
    }
    @Comment("Нижняя часть сообщения статистики")
    default List<String> getStatisticFooter() {
        return List.of("===============");
    }

    @Comment("Верхняя часть сообщения логов")
    default List<String> getLogsHeader() {
        return Arrays.asList("&eПоследние %count% логов", "&bЛоги игрока: %player%");
    }
    @Comment("Паттерн плейсхолдера координат в логах")
    default String getCoordinatesPattern() {
        return "X:%x% Y:%y% Z:%z%";
    }
    @Comment("Всплывающее сообщение при наведении на координаты")
    default String getCoordinatesHoverMessage() {
        return "&bВы телепортируетесь на данные координаты";
    }
    @Comment("Паттерн сообщения лога о добытой руде")
    default String getLogsOre() {
        return "%ore_color%%ore_type% &r- &b%coordinates%";
    }
    @Comment("Нижняя часть сообщения лога")
    default List<String> getLogsFooter() {
        return List.of("===============");
    }
}
