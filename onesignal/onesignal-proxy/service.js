const express = require("express");
const axios = require("axios");

const app = express();
app.use(express.json());


const ONESIGNAL_APP_ID = "d6359313-ac1a-496b-be12-07cd18dcfa46";
const ONESIGNAL_REST_API_KEY = "os_v2_app_2y2zge5mdjewxpqsa7grrxh2i3ye6vue5whebmn7s7kgc3mti37hshnpce54cqw3zwnkebf3scikm4f2sjsgdgu6i5vyrn4vloqpslq"; 


app.post("/api/notifications/invite", async (req, res) => {
  const { invitedUserUid, inviterName, allianceName, inviterUid} = req.body;

  console.log("Received invite request:", req.body)

  if (!invitedUserUid || !inviterName || !allianceName) {
    return res.status(400).json({ error: "Missing fields" });
  }

  try {
    const response = await axios.post(
      "https://onesignal.com/api/v1/notifications",
      {
        app_id: ONESIGNAL_APP_ID,
        include_external_user_ids: [invitedUserUid],
        headings: { en: "New alliance invite" },
        contents: { en: `${inviterName} invited you to an alliance -  '${allianceName}'!` },
        buttons: [
          { id: "accept", text: "Accept" },
          { id: "decline", text: "Decline" }
        ],
        data: { inviterUid: inviterUid },
        persist_notification: true

      },
      {
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Basic ${ONESIGNAL_REST_API_KEY}`
        }
      }
    );

    res.json(response.data);
  } catch (err) {
    console.error(err.response ? err.response.data : err.message);
    res.status(500).send("Error sending notification");
  }
});


app.post("/api/notifications/respond", async (req, res) => {
  const { invitedUserUid, action, inviterUid } = req.body;

  if (!invitedUserUid || !action) {
    return res.status(400).json({ error: "Missing fields" });
  }

  if (action === "accept" && inviterUid) {
    await axios.post(
      "https://onesignal.com/api/v1/notifications",
      {
        app_id: ONESIGNAL_APP_ID,
        include_external_user_ids: [inviterUid],
        headings: { en: "Invite accepted" },
        contents: { en: `${invitedUserUid} accepted your invite to join the alliance.` }
      },
      {
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Basic ${ONESIGNAL_REST_API_KEY}`
        }
      }
    );
  }

  res.json({ ok: true });
});

app.listen(3001, () => console.log("Server running on port 3001"));