# Перечень сущностей системы

- игрок (player)
- лобби (lobby)
- доска (board, игра 1х1, по разным правилам 
- комманда (team, x игроков + y запасных)
- тур (tour, x досок)
- командный тур (ctour, х игроков * у комманд)
- турнир (tournament, x туров, по разным правилам (preSets))
- членство в лобби (lobbyMemeberShip)
- приглашение в лобби (lobbyInvite)
- пресет (набор предустановленных правил игры)

# Общие требования к системе


## общие требование к сущности "игрок"

- зарегистрировать в системе (через код активации на почту)
- войти в систему через google, facebook
- забанить или разбанить игрока
- возможность принудительно сменить пароль
- возможность сменить почтовый ящик через код активации на почту

## общие требование к сущности "анонимный игрок"

- наличие возможности сыграть в песочниче анонимному (незарегистрированному) пользователю с ботом или другим анонимный пользователем
- анонимный игрок не имеет пароля
- анонимный игрок может стать полноценным зарегистрированным игроком
- анонимный игрок привязан к конкретному устройсу (userAgent & ip => claimsJwt), localStorage
- анонимный игрок привязан иеет дефолтный логин (login+id, например login255, login1024)

## общие требование к сущности "лобби"

- у каждого игрока должно существовать персональное лобби
- лобби может быть открытым (доступно) или приватным (только по приглашению)
- статус лобби может быть изменен по желанию его владельца
- (?) ананоминый игрок не может иметь своего лобби
- (?) ананоминый игрок не может быть членом другого лобби
- игрок иметь членство в нескольких лобби одноременно

## общие требование к сущности "пресет" (preSet)

- пресет состоит из коллекции настроечных параметров игры:

### атрибуты партии (игры)
- (?) длительность партии, мин
- (?) алгоритм подсчета оставшегося времени, енум, хардкод
- (?) тип игры (классика, шведки), енум, хардкод
