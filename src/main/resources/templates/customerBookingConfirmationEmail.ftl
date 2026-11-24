<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Enquiry Received</title>
    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color: #f5f7fa;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 650px;
            margin: 25px auto;
            background: #ffffff;
            border-radius: 6px;
            padding: 25px;
            box-shadow: 0 1px 6px rgba(0,0,0,0.08);
        }

        h2 {
            color: #0b5ed7;
            margin-bottom: 10px;
        }

        .section-title {
            margin-top: 20px;
            margin-bottom: 5px;
            font-weight: bold;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .info-table td {
            padding: 8px 5px;
            border-bottom: 1px solid #eaeaea;
        }

        .label {
            width: 40%;
            font-weight: bold;
            color: #555;
        }

        .footer {
            margin-top: 25px;
            font-size: 12px;
            color: #777;
            text-align: center;
        }
    </style>
</head>

<body>
<div class="container">

    <h2>Enquiry Received</h2>

    <p>Dear <strong>${customerName}</strong>,</p>

    <p>
        Thank you for your enquiry.  
        Our team has received your request and will contact you shortly with more details.
    </p>

    <div class="section-title">Enquiry Details</div>

    <table class="info-table">
        <tr>
            <td class="label">Package ID / Trip</td>
            <td>${packageId}</td>
        </tr>
        <tr>
            <td class="label">Package Name</td>
            <td>${packageName}</td>
        </tr>
        <tr>
            <td class="label">Adults</td>
            <td>${numberOfAdults}</td>
        </tr>
        <tr>
            <td class="label">Childs</td>
            <td>${numberOfChildren}</td>
        </tr>
        <tr>
            <td class="label">Contact Number</td>
            <td>${phone}</td>
        </tr>
    </table>

    <p style="margin-top: 20px;">
        Our travel consultant will connect with you for prices, itinerary options,  
        and the best recommendations for your trip.
    </p>

    <p>
        Regards,<br>
        <strong>Weeky World Tour Support Team</strong><br>
        support@weekytrip.com , +917204536179 , +91 7410011180
    </p>

    <div class="footer">
        © ${.now?string("2026")} Weeky World Tour. All rights reserved.
    </div>

</div>
</body>
</html>
