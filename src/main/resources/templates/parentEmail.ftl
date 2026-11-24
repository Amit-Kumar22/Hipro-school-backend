html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Parent Account Registration</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #f4f6f8;
            font-family: Arial, Helvetica, sans-serif;
            color: #333333;
        }

        .wrapper {
            width: 100%;
            padding: 30px 0;
        }

        .container {
            max-width: 620px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
        }

        .header {
            background: #1f4e79;
            padding: 28px 30px;
            text-align: center;
            color: #ffffff;
        }

        .header h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 600;
        }

        .header p {
            margin: 8px 0 0;
            font-size: 14px;
            opacity: 0.9;
        }

        .content {
            padding: 35px 35px 25px;
        }

        .content h2 {
            margin-top: 0;
            color: #1f4e79;
            font-size: 21px;
        }

        .content p {
            font-size: 15px;
            line-height: 1.7;
            margin: 12px 0;
        }

        .credentials {
            margin: 25px 0;
            border: 1px solid #dfe5eb;
            border-radius: 8px;
            overflow: hidden;
        }

        .credentials-title {
            background: #f1f5f9;
            padding: 13px 18px;
            font-size: 15px;
            font-weight: bold;
            color: #1f4e79;
        }

        .credential-row {
            padding: 13px 18px;
            border-top: 1px solid #e5e7eb;
            font-size: 14px;
        }

        .credential-label {
            display: inline-block;
            width: 130px;
            font-weight: bold;
            color: #555555;
        }

        .credential-value {
            color: #111827;
            font-family: monospace;
            font-size: 14px;
        }

        .warning {
            background: #fff7ed;
            border-left: 4px solid #f97316;
            padding: 14px 16px;
            margin: 22px 0;
            font-size: 13px;
            line-height: 1.6;
            color: #7c2d12;
        }

        .button-wrapper {
            text-align: center;
            margin: 28px 0;
        }

        .button {
            display: inline-block;
            background: #1f4e79;
            color: #ffffff !important;
            text-decoration: none;
            padding: 13px 28px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: bold;
        }

        .support {
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
        }

        .footer {
            background: #f8fafc;
            padding: 20px 30px;
            text-align: center;
            color: #6b7280;
            font-size: 12px;
            line-height: 1.6;
        }

        @media only screen and (max-width: 650px) {
            .container {
                width: 94%;
            }

            .content {
                padding: 25px 20px;
            }

            .credential-label {
                display: block;
                width: auto;
                margin-bottom: 5px;
            }
        }
    </style>
</head>

<body>

<div class="wrapper">

    <div class="container">

        <!-- Header -->
        <div class="header">

            <h1>School Management Portal</h1>

            <p>Parent Account Registration</p>

        </div>

        <!-- Content -->
        <div class="content">

            <h2>Welcome, ${Name!"Parent"}!</h2>

            <p>
                We are pleased to inform you that a parent account has been
                successfully created for the school management portal.
            </p>

            <p>
                Your account is associated with the student
                <strong>${studentName!"your child"}</strong>.
            </p>

            <!-- Credentials -->
            <div class="credentials">

                <div class="credentials-title">
                    Your Login Credentials
                </div>

                <div class="credential-row">

                    <span class="credential-label">
                        Login ID
                    </span>

                    <span class="credential-value">
                        ${loginId!""}
                    </span>

                </div>

                <div class="credential-row">

                    <span class="credential-label">
                        Temporary Password
                    </span>

                    <span class="credential-value">
                        ${code!""}
                    </span>

                </div>

            </div>

            <!-- Security Warning -->
            <div class="warning">

                <strong>Security Notice:</strong><br>

                This password is temporary. Please log in using the credentials
                above and change your password immediately after your first login.
                Do not share your login credentials with anyone.

            </div>

            <!-- Login Button -->
            <div class="button-wrapper">

                <a href="${loginUrl!""}" class="button">
                    Login to Parent Portal
                </a>

            </div>

            <p>
                Through the parent portal, you may be able to view your child's
                academic information, attendance, examination results, fees,
                notices, and other school-related information.
            </p>

            <div class="support">

                <p>
                    If you did not expect this registration or believe that
                    this email was sent to you by mistake, please contact the
                    school administration.
                </p>

                <p>
                    Regards,<br>
                    <strong>School Administration</strong>
                </p>

            </div>

        </div>

        <!-- Footer -->
        <div class="footer">

            <strong>School Management Portal</strong><br>

            This is an automated email. Please do not reply directly to this
            message unless instructed by the school administration.

            <br><br>

            &copy; ${currentYear!"2026"} School Management Portal.
            All rights reserved.

        </div>

    </div>

</div>

</body>
</html>
