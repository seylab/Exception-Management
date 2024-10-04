
# Spring Boot @Scheduled Anotasyonu

`@Scheduled` anotasyonu, Spring Boot uygulamalarında zamanlanmış görevler (scheduled tasks) oluşturmak için kullanılır. Uygulamanın belirli bir zaman diliminde veya periyodik olarak çalışmasını sağlayan basit bir yol sunar.

## 1. Genel Kullanım

Spring Boot projelerinde zamanlanmış görevleri etkinleştirmek için öncelikle uygulamanın ana sınıfına veya yapılandırma sınıfına `@EnableScheduling` anotasyonu eklenmelidir.

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Ardından, herhangi bir `@Component` anotasyonuna sahip sınıf içinde `@Scheduled` anotasyonunu kullanarak zamanlanmış bir görev tanımlayabiliriz.

## 2. @Scheduled Anotasyonu Parametreleri

### 2.1. `fixedRate`

`fixedRate`, belirtilen milisaniye cinsinden süre ile periyodik olarak görevlerin çalışmasını sağlar. Her görev, önceki görevin başlama zamanına bağlı olarak çalıştırılır.

```java
@Scheduled(fixedRate = 5000) // Görev her 5 saniyede bir çalışır.
public void scheduleTaskWithFixedRate() {
    System.out.println("Bu görev her 5 saniyede bir çalışır.");
}
```

### 2.2. `fixedDelay`

`fixedDelay`, bir görev tamamlandıktan sonra belirtilen milisaniye kadar bekleyip tekrar çalıştırılır. Yani, önceki görevin bitişine göre gecikme hesaplanır.

```java
@Scheduled(fixedDelay = 5000) // Görev tamamlandıktan 5 saniye sonra tekrar çalışır.
public void scheduleTaskWithFixedDelay() {
    System.out.println("Bu görev tamamlandıktan 5 saniye sonra tekrar çalışır.");
}
```

### 2.3. `initialDelay`

`initialDelay`, görev ilk başlatıldığında ne kadar süre sonra çalışacağını belirtir. Genellikle `fixedRate` veya `fixedDelay` ile birlikte kullanılır.

```java
@Scheduled(fixedRate = 5000, initialDelay = 10000) // İlk çalıştırma 10 saniye sonra, sonrasında her 5 saniyede bir.
public void scheduleTaskWithInitialDelay() {
    System.out.println("Bu görev ilk olarak 10 saniye sonra çalışır ve her 5 saniyede bir tekrar eder.");
}
```

### 2.4. `cron`

`cron` ifadesi, cron formatında zamanlama yapmanızı sağlar. Bu, görevlerin belirli bir zaman diliminde veya belirli zaman aralıklarında çalışmasını sağlar.

```java
@Scheduled(cron = "0 0 12 * * ?") // Her gün öğlen 12:00'da çalışır.
public void scheduleTaskWithCronExpression() {
    System.out.println("Bu görev her gün öğlen 12:00'da çalışır.");
}
```

#### Cron İfadesi Formatı:

Cron ifadesi 6 alan içerir:
```
┌───────────── saniye (0 - 59)
│ ┌───────────── dakika (0 - 59)
│ │ ┌───────────── saat (0 - 23)
│ │ │ ┌───────────── ayın günü (1 - 31)
│ │ │ │ ┌───────────── ay (1 - 12)
│ │ │ │ │ ┌───────────── haftanın günü (0 - 7) (Pazar=0 veya 7)
│ │ │ │ │ │
* * * * * *
```

Örnek Cron İfadeleri:
- `"0 0 12 * * ?"` – Her gün saat 12:00'da çalışır.
- `"0 15 10 * * ?"` – Her gün saat 10:15'te çalışır.
- `"0 0/5 14 * * ?"` – Her gün 14:00 ile 14:55 arası, her 5 dakikada bir çalışır.
- `"0 0 14 * * MON-FRI"` – Hafta içi her gün saat 14:00'da çalışır.

## 3. Dikkat Edilmesi Gerekenler

- Zamanlanmış görevler varsayılan olarak tek bir iş parçacığı (`single-threaded`) içinde çalışır. Eğer birden fazla zamanlanmış göreviniz varsa, görevler birer birer çalıştırılır. Eğer aynı anda birden fazla görevin çalışmasını istiyorsanız, `TaskExecutor` yapılandırması yapılmalıdır.
  
- Uzun süren görevler `fixedRate` veya `fixedDelay` ile kullanıldığında, görevler çakışabilir. Bunun için `@Async` anotasyonu ile görevleri asenkron hale getirebilirsiniz.

```java
@Scheduled(fixedRate = 5000)
@Async
public void asyncTask() {
    System.out.println("Asenkron olarak çalışıyor.");
}
```

## 4. Örnek Zamanlanmış Görevler

### 4.1. Basit Görev

```java
@Component
public class SimpleScheduledTask {

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.println("Şu anki zaman: " + new Date());
    }
}
```

### 4.2. Cron İle Görev Zamanlama

```java
@Component
public class CronScheduledTask {

    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void performTask() {
        System.out.println("Hafta içi her gün sabah 9'da çalışıyorum.");
    }
}
```

## 5. Kaynaklar

- [Spring Scheduling Annotations Documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Scheduled.html)
- [Spring Cron Expressions Guide](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html)

---

Bu notlar, Spring Boot uygulamalarında zamanlanmış görevlerin nasıl kullanılacağına dair temel bilgileri içermektedir.
