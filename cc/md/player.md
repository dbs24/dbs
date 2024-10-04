# Требования к атрибутам сущности "Player"


| N | GrpcId|  Атрибут|     Описание      | Тип/домен |  Nullable |
:----------|---|---|:----------------|--|-----------|
| 100100 ||  ||||
| |100100001  |player_id| - id| tidbigcode/EntityId |  |
| |100100003  |player_login| - логин| tstr100/PlayerLogin | +  |
| |100100005  |email| - email | tstr200/Email | +  |
| |100100006  |phone| - phone | tstr100/String | +  |
| |100100007  |first_name| - first_name | tstr100/String | +  |
| |100100008  |middle_name| - middle_name | tstr100/String | +  |
| |100100009  |last_name| - last_name | tstr100/String | +  |
| |100100010  |password_hash| - пароль| tstr120/Password | +  |
| |100100011  |birth_date| - birth_date | tdate/BirthDate | +  |
| |100100012  |country| - country | tstr3/CountryIsoCode | +  |
| |100100013  |gender| - gender | tstr2/String | +  |
| |100100014  |avatar_path| - avatar_path | tstr200/UriPath | +  |
| |100100015  |small_avatar_path| - small_avatar_path | tstr200/UriPath | +  |

# Требования к статусам сущности "Player" - (100100016)

| N | Статусы | Наименование |
----------|---|-----------|
| 10010001 | ACTUAL | Действующий|
| 10010002 | CLOSED | Закрытый|
| 10010017 | BANNED | Бан|
| 10010050 | ANONYMOUS | Анонимный|

# Допустимые смены статуса сущности "Player"

| Возможные переходы |  из | в |
----------|---|-----------|
|  | ANONYMOUS | ACTUAL|
|  | ANONYMOUS | CLOSED|
|  | ANONYMOUS | BANNED|
|  | ACTUAL | BANNED|
|  | ACTUAL | CLOSED|
|  | CLOSED | ACTUAL|
|  | BANNED | ACTUAL|


# Требования к действиям над сущностью "Player"

| N | Наименование |  Примечание |
----------|---|-----------|
| 1001000010 | Создание или изменение игрока | |
| 1001000020 | Изменение статуса игрока | |
| 1001000030 | Изменение пароля игрока | |
