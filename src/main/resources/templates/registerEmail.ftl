<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome to Weeky Restaurant CRM</title>
</head>
<body style="margin:0; padding:0; font-family: Arial, Helvetica, sans-serif; background-color:#f4f7f5;">
<table width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td align="center">
            <table width="600" cellpadding="0" cellspacing="0"
                   style="background-color:#ffffff; margin-top:20px; border-radius:6px; overflow:hidden;">

                <!-- Header -->
                <tr>
                    <td style="background-color:#2e7d32; padding:20px; text-align:center; color:#ffffff;">
                        <h2 style="margin:0;">Welcome to Weeky Restaurant CRM</h2>
                    </td>
                </tr>

                <!-- Body -->
                <tr>
                    <td style="padding:30px; color:#333333;">
                        <p style="font-size:15px;">
                            Dear <strong>${userName}</strong>,
                        </p>

                        <p style="font-size:15px; line-height:1.6;">
                            Welcome to <strong>Weeky Restaurant CRM</strong>!<br/>
                            Your registration has been completed successfully, and we’re excited to have you onboard.
                        </p>

                        <p style="font-size:15px; line-height:1.6;">
                            With <strong>Weeky Restaurant CRM</strong>, you can now manage your restaurant operations
                            smarter and faster. Here’s what you can start doing right away:
                        </p>

                        <ul style="font-size:14px; line-height:1.8; padding-left:18px;">
                            <li>Manage orders and billing seamlessly</li>
                            <li>Track sales and daily reports</li>
                            <li>Control menu items and pricing</li>
                            <li>Manage staff and roles</li>
                            <li>Monitor inventory and stock levels</li>
                        </ul>

                        <div style="background-color:#e8f5e9; border-left:4px solid #2e7d32;
                                    padding:15px; margin:20px 0;">
                            <p style="margin:0; font-size:14px; color:#2e7d32;">
                                <strong>Login Details</strong><br/><br/>
                                🔗 <strong>Login URL:</strong>
                                <a href="${loginUrl}" style="color:#2e7d32; text-decoration:none;">
                                    ${loginUrl}
                                </a><br/>
                                📧 <strong>Registered Email:</strong> ${userEmail}
                                <br/>
                               📧 <strong>Your Password:</strong> ${password}
                            </p>
                        </div>

                        <p style="font-size:14px; line-height:1.6;">
                            To help you get started, we recommend:
                        </p>

                        <ol style="font-size:14px; line-height:1.8; padding-left:18px;">
                            <li>Completing your restaurant profile</li>
                            <li>Adding your menu items</li>
                            <li>Setting up taxes, discounts, and payment options</li>
                        </ol>

                        <p style="font-size:14px; line-height:1.6;">
                            If you need any help, our support team is always here for you.
                        </p>

                        <p style="font-size:14px;">
                            📞 <strong>Support:</strong> ${supportPhone}<br/>
                            📧 <strong>Email:</strong>
                            <a href="mailto:${supportEmail}"
                               style="color:#2e7d32; text-decoration:none;">
                                ${supportEmail}
                            </a>
                        </p>

                        <p style="margin-top:30px; font-size:14px;">
                            Warm regards,<br/>
                            <strong>${senderName}</strong><br/>
                            <strong>Weeky Team</strong><br/>
                            <a href="${websiteUrl}" style="color:#2e7d32; text-decoration:none;">
                                ${websiteUrl}
                            </a>
                        </p>
                    </td>
                </tr>

                <!-- Footer -->
                <tr>
                    <td style="background-color:#f1f8f4; padding:15px; text-align:center;
                               font-size:12px; color:#777777;">
                        © 2025 Weeky. All rights reserved.
                    </td>
                </tr>

            </table>
        </td>
    </tr>
</table>
</body>
</html>