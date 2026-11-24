<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Khana Mart - Password Reset</title>
</head>

<body style="margin:0; padding:0; background-color:#f4f9f4; font-family:'Segoe UI', Arial, sans-serif;">

  <table align="center" width="100%" cellpadding="0" cellspacing="0"
         style="max-width:600px; margin:40px auto; background-color:#ffffff;
         border-radius:12px; box-shadow:0 6px 18px rgba(0,0,0,0.08); overflow:hidden;">

    <!-- Header -->
    <tr>
      <td style="background:linear-gradient(135deg,#2ecc71,#27ae60);
                 padding:30px; text-align:center;">
        <h1 style="color:#ffffff; margin:0; font-size:26px;">
          Khana Mart
        </h1>
        <p style="color:#eaffea; margin-top:6px; font-size:14px;">
          Fresh groceries delivered to your door
        </p>
      </td>
    </tr>

    <!-- Body -->
    <tr>
      <td style="padding:35px 30px;">

        <h2 style="color:#2c3e50; margin-top:0;">
          Hello ${Name},
        </h2>

        <p style="color:#555555; font-size:16px; line-height:1.6;">
          We received a request to reset your Khana Mart account password.
          Please use the verification code below to proceed with resetting your password.
        </p>

        <!-- OTP Box -->
        <div style="text-align:center; margin:35px 0;">
          <span style="
              background-color:#e8f8f0;
              color:#27ae60;
              padding:16px 30px;
              font-size:24px;
              border-radius:8px;
              letter-spacing:4px;
              font-weight:bold;
              display:inline-block;
              border:2px dashed #2ecc71;">
              ${code}
          </span>
        </div>

        <p style="color:#666666; font-size:14px;">
          This code is valid for the next <strong>10 minutes</strong>.
          Please do not share this code with anyone.
        </p>

        <p style="color:#666666; font-size:14px;">
          If you did not request this password reset, you can safely ignore this email.
        </p>

        <hr style="margin:30px 0; border:none; border-top:1px solid #eeeeee;">

        <p style="font-size:14px; color:#777777;">
          Need help? Contact us at
          <a href="mailto:support@khanamart.com"
             style="color:#27ae60; text-decoration:none;">
             support@khanamart.com
          </a>
        </p>

        <p style="font-size:14px; color:#777777;">
          Thank you,<br>
          <strong style="color:#27ae60;">Khana Mart Team</strong>
        </p>

      </td>
    </tr>

    <!-- Footer -->
    <tr>
      <td style="background-color:#f1fdf5; text-align:center;
                 padding:18px; font-size:12px; color:#999999;">
        © 2026 Khana Mart. All rights reserved.
      </td>
    </tr>

  </table>

</body>
</html>
