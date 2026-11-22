const passengerList = document.querySelector("#passenger-list");
const queueCount = document.querySelector("#queue-count");
const statusMessage = document.querySelector("#status-message");

let passengers = [];
let remainingPassengerCount = 0;
let queueLoaded = false;
let requestInProgress = false;
let priorityUpdateQueue = Promise.resolve();
let latestPriorityUpdate = 0;

function renderPassengers() {
  passengerList.replaceChildren(...passengers.map((passenger, index) => {
    const row = document.createElement("li");
    row.className = "passenger-row";

    const number = document.createElement("span");
    number.className = "queue-number";
    number.setAttribute("aria-hidden", "true");
    number.textContent = String(index + 1).padStart(2, "0");

    const button = document.createElement("button");
    button.className = "passenger-button";
    button.type = "button";
    button.dataset.boardingPassNumber = passenger.boardingPassNumber;
    button.disabled = requestInProgress;

    const seniorCitizenStatus = passenger.age >= 58 ? "Yes" : "No";
    const veteranStatus = passenger.veteran ? "Yes" : "No";
    button.setAttribute(
      "aria-label",
      `Board ${passenger.passengerName}. Zone ${passenger.zone}. Veteran ${veteranStatus}. Senior citizen ${seniorCitizenStatus}.`
    );

    const details = document.createElement("span");
    details.className = "passenger-details";

    const name = document.createElement("span");
    name.className = "passenger-name";
    name.textContent = passenger.passengerName;

    const metadata = document.createElement("span");
    metadata.className = "passenger-meta";
    metadata.textContent = `Zone: ${passenger.zone} · Veteran: ${veteranStatus} · Senior citizen: ${seniorCitizenStatus}`;

    const action = document.createElement("span");
    action.className = "passenger-action";
    action.textContent = "BOARD";

    details.append(name, metadata);
    button.append(details, action);
    button.addEventListener("click", () => boardPassenger(passenger.boardingPassNumber));

    const arrow = document.createElement("span");
    arrow.className = "row-arrow";
    arrow.setAttribute("aria-hidden", "true");
    arrow.textContent = "›";

    row.append(number, button, arrow);
    return row;
  }));
  queueCount.textContent = queueLoaded ? String(remainingPassengerCount).padStart(2, "0") : "—";
}

async function refreshBoardingQueue() {
  const response = await fetch("/api/passengers/boarding?limit=5");
  if (!response.ok) {
    throw new Error(`Queue request failed with status ${response.status}`);
  }

  const queue = await response.json();
  passengers = queue.passengers;
  remainingPassengerCount = queue.remainingPassengerCount;
  queueLoaded = true;
  renderPassengers();
}

function getPriorityPreferences() {
  const formData = new FormData(document.querySelector("#priority-form"));
  return {
    seniorCitizen: formData.get("seniorCitizen") === "true",
    veteran: formData.get("veteran") === "true"
  };
}

function savePriorityPreferences(preferences, updateId) {
  let preferencesSaved = false;

  return fetch("/api/boarding-priority", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(preferences)
  }).then((response) => {
    if (!response.ok) {
      throw new Error(`Priority update failed with status ${response.status}`);
    }
    return response.json();
  }).then(async () => {
    preferencesSaved = true;
    await refreshBoardingQueue();
    if (updateId === latestPriorityUpdate) {
      statusMessage.dataset.state = "success";
      statusMessage.textContent = "Priority selections saved and passenger list refreshed.";
    }
  }).catch((error) => {
    console.error("Unable to save priority selections or refresh the boarding queue", error);
    if (updateId === latestPriorityUpdate) {
      statusMessage.dataset.state = "error";
      statusMessage.textContent = preferencesSaved
        ? "Priority saved, but the passenger list could not be refreshed. Please try again."
        : "Could not save priority selections. Please try again.";
    }
  });
}

document.querySelector("#priority-form").addEventListener("change", () => {
  const updateId = ++latestPriorityUpdate;
  const preferences = getPriorityPreferences();
  statusMessage.dataset.state = "";
  statusMessage.textContent = "Saving priority selections…";
  priorityUpdateQueue = priorityUpdateQueue.then(() => savePriorityPreferences(preferences, updateId));
});

async function boardPassenger(boardingPassNumber) {
  if (requestInProgress) return;
  if (!passengers.some((passenger) => passenger.boardingPassNumber === boardingPassNumber)) return;

  requestInProgress = true;
  statusMessage.dataset.state = "";
  statusMessage.textContent = "Sending boarding request…";
  renderPassengers();

  let passengerBoarded = false;
  try {
    const response = await fetch(
      `/api/passengers/${encodeURIComponent(boardingPassNumber)}/board`,
      { method: "POST" }
    );
    if (!response.ok) {
      throw new Error(`Boarding request failed with status ${response.status}`);
    }
    passengerBoarded = true;
    await refreshBoardingQueue();
    statusMessage.dataset.state = "success";
    statusMessage.textContent = `Passenger boarded. ${remainingPassengerCount} passengers remain in the queue.`;
  } catch (error) {
    console.error("Unable to process the boarding request", error);
    statusMessage.dataset.state = "error";
    statusMessage.textContent = passengerBoarded
      ? "Passenger boarded, but the queue could not be refreshed. Please reload the page."
      : "The boarding request failed. Please try again.";
  } finally {
    requestInProgress = false;
    renderPassengers();
  }
}

async function initializeBoardingQueue() {
  requestInProgress = true;
  statusMessage.textContent = "Loading next passengers…";
  renderPassengers();
  try {
    await refreshBoardingQueue();
    statusMessage.textContent = remainingPassengerCount > 0
      ? `Showing the next ${passengers.length} of ${remainingPassengerCount} passengers.`
      : "No passengers are waiting to board.";
  } catch (error) {
    console.error("Unable to load the boarding queue", error);
    statusMessage.dataset.state = "error";
    statusMessage.textContent = "Could not load the boarding queue. Please reload the page.";
  } finally {
    requestInProgress = false;
    renderPassengers();
  }
}

initializeBoardingQueue();
