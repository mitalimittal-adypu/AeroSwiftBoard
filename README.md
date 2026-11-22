# AeroSwift Boarding Gate

A single-page flight boarding display backed by a Java Spring Boot application. It loads passenger details from a CSV resource, shows the next five passengers in boarding priority order, and removes passengers from the queue when they board.

## Requirements

- Java 25
- No separate Maven installation is needed; the project includes the Maven Wrapper.

## Run the application

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) to use the boarding screen.

To run the tests:

```powershell
.\mvnw.cmd test
```

On macOS or Linux, use `./mvnw` in place of `.\mvnw.cmd`.

## Boarding screen

- On page load, the UI requests the next five passengers from the backend.
- Each passenger button displays their name, zone, veteran status, and senior-citizen status. Senior citizen is shown as `Yes` when the passenger's age is 58 or above; the age itself is not displayed.
- Selecting a passenger to board removes them from the in-memory queue, then the UI requests the next five passengers.
- Changing the Senior citizen or Veteran priority option saves the selection and refreshes the displayed passenger list.

## REST API

### Get passengers to board

```http
GET /api/passengers/boarding?limit=5
```

The `limit` parameter is optional and defaults to `5`. It must be greater than zero.

Example response:

```json
{
  "passengers": [
    {
      "passengerName": "Mei Khan",
      "boardingPassNumber": "BP2263132191",
      "seatNumber": "16C",
      "zone": "C",
      "age": 11,
      "veteran": false
    }
  ],
  "remainingPassengerCount": 200
}
```

The `passengers` array contains up to `limit` passengers. `remainingPassengerCount` is the total number still in the queue, not just the number returned.

### Board a passenger

```http
POST /api/passengers/{boardingPassNumber}/board
```

Removes the matching passenger from the in-memory queue and returns `204 No Content`. Returns `404 Not Found` if that boarding pass is not in the queue.

### Update priority selection

```http
POST /api/boarding-priority
Content-Type: application/json

{
  "seniorCitizen": true,
  "veteran": false
}
```

Both boolean fields are required. The endpoint stores the latest selection in a Spring singleton and returns it. This selection is held in memory and resets when the application restarts.

## Boarding priority

The application selects a comparator based on the saved priority selection:

| Senior citizen | Veteran | Comparator |
|---|---|---|
| No | No | Default |
| Yes | No | Senior citizen |
| Yes or No | Yes | Senior citizen and veteran |

Comparator order:

- **Default:** `Business_Class`, Zone C, Zone B, Zone A.
- **Senior citizen:** passengers aged 58 or above first, then the default order.
- **Senior citizen and veteran:** senior citizens first, then veterans, then the default order.

The priority selection affects which comparator orders the queue; the boarding API still removes passengers only when their button is selected.

## Passenger data and state

Passenger data is read from `src/main/resources/passengers.csv` at application startup. The CSV columns are passenger name, boarding pass number, seat number, zone, age, and veteran status (`Yes` or `No`).

The passenger queue and priority selections are stored in singleton in-memory services. Boarding changes are not persisted to the CSV or another database, so restarting the application reloads the original passenger list.

## Project layout

```text
src/main/java/com/adypu/aeroswiftboard/
  comparator/     Boarding priority comparators
  controller/     REST API endpoints
  model/          Passenger and API response/request data
  service/        CSV loading, passenger queue, and selection state
src/main/resources/
  passengers.csv  Passenger data loaded at startup
  static/         Single-page HTML, CSS, and JavaScript UI
```
