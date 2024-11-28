makarenko-java / com.javarush.makarenko.island
---
**Проект Остров**
----

**Иерархия классов**
- Animal
  - Herbivore
    - 🐎 Horse - Лошадь
    - 🦌 Deer - Олень
    - 🐇 Rabbit - Кролик
    - 🐁 Mouse - Мышь
    - 🐐 Goat - Коза 
    - 🐑 Sheep - Овца
    - 🐗 Boar - Кабан
    - 🐃 Buffalo - Буйвол
    - 🦆 Duck - Утка
    - 🐛 Caterpillar - Гусеница
  - Predator
    - 🐺 Wolf - Волк
    - 🐍 Boa - Удав
    - 🦊 Fox - Лиса
    - 🐻 Bear - Медведь
    - 🦅 Eagle - Орел
- Plant - Растение <u><b>НЕ РЕАЛИЗОВАНО</b></u>

___

Каждое животное обладает следующими полями:
- weight - Вес одного животного;
- maxPerCell - Максимальное количество животных этого вида на одной клетке;
- maxSpeed - Скорость перемещения, не более чем, клеток за ход;
- foodNeededForMaxSatiety - Сколько килограммов пищи нужно животному для полного насыщения;
- consumptionProbability - Таблица вероятностей поедания одних видов другими;
- currentSatiety - Текущая сытость <u><b>НЕ РЕАЛИЗОВАНО</b></u>.

Поля считываются из таблиц данных, заданных в условии.

Таблица характеристик животных [animal_characteristics_table.yaml](src/main/resources/animal_characteristics_table.yaml). Её данные хранит класс AnimalCharacteristicsTable.

Таблица вероятностей поедания одних видов другими [consumption_probability_table.yaml](src/main/resources/consumption_probability_table.yaml). Её данные хранит класс ConsumptionProbabilityTable.

Для инициализации таблиц создан класс TableInitializer и запуск метода initialize() приводит к инициализации полей классов AnimalCharacteristicsTable и ConsumptionProbabilityTable.

---

Каждое животное обладает следующими методами (за исключением геттеров):
- eat() - Покушать <u><b>НЕ РЕАЛИЗОВАНО</b></u>.
- reproduce() - Размножиться <u><b>НЕ РЕАЛИЗОВАНО</b></u>.
- move() - Перемещаться <u><b>НЕ РЕАЛИЗОВАНО</b></u>.
- chooseDirection() - Выбрать направление передвижения <u><b>НЕ РЕАЛИЗОВАНО</b></u>.

---

Класс Settings отвечает за настройки программы, которые не упомянуты в таблицах.

На данный момент этот класс содержит следующие константы:
- ANIMAL_POPULATION_SCALE - Коэффициент масштабируемости количества животных для заселения клетки;
- THREAD_POOL_SIZE - Максимальное количество потоков в пуле для обработки задач; 
- ISLAND_ROWS, ISLAND_COLUMNS - Размеры острова;
- SIMULATION_DAYS - Количество дней симуляции.

---

Класс Cell имплементирует Runnable

ANIMAL_CLASSES - Константа - список всех классов животных, который заполняется в статическом блоке.

animals - Поле - список всех имеющихся животных, который заполняется при вызове метода класса populateAnimals().

populateAnimals() - Метод, заполняющий поле animals животными используя список классов животных ANIMAL_CLASSES через getDeclaredConstructor().newInstance()
с константой Settings.ANIMAL_POPULATION_SCALE, параметром maxPerCell для каждого класса из исходных данных и функцией Math.random().

run() - Метод интерфейса Runnable, листинг ниже <u><b>МЕТОД БУДЕТ ДОРАБОТАН</b></u>

    @Override
    public void run() {
        for (Animal animal : animals) {
            animal.eat();
            animal.reproduce();
            animal.chooseDirection();
            animal.move();
        }

---

Класс Island

В классе в конструкторе создается поле Cell[][] field и заполняется экземплярами Cell.

---

Класс IslandSimulation

Данный класс реализует многопоточность.

Создается ExecutorService executorService следующей командрйExecutors.newFixedThreadPool(Settings.THREAD_POOL_SIZE).

Имеется метод startSimulation, который принимает на вход количество дней, после которых симуляция будет остановлена и в этом методе
и происходит вся симуляция. В нем в начале каждого дня происходит вывод на консоль номера дня и списка всех имеющихся классов животных
и их количества по классам на всем острове.

executorService запускает метод submit(), в который передается конкретная клетка острова и при этом результат вызова метода
сохраняется в список объектов Future<?> для последующего правильного завершения обработки всех клеток, т.к. при переборе списка объектов
Future<?> у каждого вызывается метод get(), что позволяет убедиться в том, что работа клетки была завершена и когда мы пройдем весь список
объектов Future<?> - можно начинать новый день либо описать рост травы и подведение статистики.

Имеется метод collectAnimalStatistics(), который проходится по всем клеткам острова и считает суммарное число животных каждого вида и
возвращаемый результат представляет собой Map<String, Integer>.

---

ScheduledExecutorService - <u><b>НЕ РЕАЛИЗОВАНО</b></u>


Синхронизация с ScheduledExecutorService - <u><b>НЕ РЕАЛИЗОВАНО</b></u>